# Binance Migration + Product Detail + Order Placement — Design

**Date:** 2026-06-24
**Status:** Approved
**Author:** Prateek Sharma (with Claude)

## Goal

Move CryptoTracker off CoinGecko and onto Binance APIs, then build a Binance-style
product detail page and an order placement page. The app shifts from a *coin information*
model to a *trading exchange* model.

## Division of labor

- **Claude implements Phase 1** (Binance market-data migration). End state: app builds,
  runs against Binance, tests green.
- **Prateek implements Phases 2–5** from this spec (WebSocket, detail screen, chart, order
  book, order placement). Order placement targets **Binance Testnet** (real signed requests).

## The core tension

CoinGecko is a *coin information* API: names, logos, market cap, ATH, descriptions.
Binance is a *trading exchange* API: it knows **symbols** (`BTCUSDT`), prices, volumes,
candles, and order books — but has **no** coin names, logos, market caps, ATH, or
descriptions.

Consequence: this is not a 1:1 field swap. It is a **model reshape** from "coin" to
"trading pair." Fields with no Binance source are dropped and replaced with trading-native
fields:

| Dropped (no Binance source) | Replaced / added (Binance-native)        |
|-----------------------------|-------------------------------------------|
| `imageUrl` (logo)           | base/quote asset labels (BTC / USDT)      |
| `marketCapUsd`              | `quoteVolume` (24h quote volume)          |
| `marketCapRank`             | sort position by quote volume             |
| `athUsd`                    | `highPrice` / `lowPrice` (24h)            |
| `description`               | — (no equivalent; section removed)        |

## Architecture overview

Existing multi-module structure is kept:

```
app
core:domain        — models + repository interfaces (pure Kotlin)
core:network       — HTTP stack (Retrofit + Moshi + OkHttp); gains WebSocket provider
core:data          — Binance API, DTOs, mappers, repository impls
core:designsystem  — theme, formatters, shared composables
feature:coinlist   — pair list (renamed conceptually to "market list")
feature:coindetail — pair detail (header, chart, order book, buy/sell)
feature:order      — NEW: order placement (Testnet)
```

Base URLs:
- Market data (REST): `https://api.binance.com/api/v3/`
- WebSocket: `wss://stream.binance.com:9443/ws/`
- Testnet trading (REST, signed): `https://testnet.binance.vision/api/v3/`

---

## Phase 1 — Binance market-data migration (Claude)

### Endpoints

| Purpose        | Endpoint                              | Notes                                  |
|----------------|---------------------------------------|----------------------------------------|
| List           | `GET /ticker/24hr`                    | Array of ALL symbols. Filter to USDT.  |
| Detail header  | `GET /ticker/24hr?symbol=BTCUSDT`     | Single object, same fields.            |

`/ticker/24hr` (per symbol) returns at least:
`symbol`, `lastPrice`, `priceChange`, `priceChangePercent`, `highPrice`, `lowPrice`,
`volume` (base volume), `quoteVolume` (quote volume), `openPrice`, `count`.

All numeric fields arrive as **strings** (e.g. `"70712.00000000"`) — DTOs parse as `String`,
mappers convert to `Double` with safe fallback.

### Domain model reshape (`core:domain`)

Rename `Coin` → `MarketPair`, `CoinDetail` → `PairDetail`, `CoinRepository` →
`MarketRepository`. `CoinSortOrder` is removed (Binance sorts client-side by quote volume).

```kotlin
data class MarketPair(
    val symbol: String,        // "BTCUSDT" — primary id
    val baseAsset: String,     // "BTC"
    val quoteAsset: String,    // "USDT"
    val lastPrice: Double,
    val priceChangePercent: Double,
    val quoteVolume: Double,   // for sorting + display
)

data class PairDetail(
    val symbol: String,
    val baseAsset: String,
    val quoteAsset: String,
    val lastPrice: Double,
    val priceChangePercent: Double,
    val high24h: Double,
    val low24h: Double,
    val volume: Double,        // base volume
    val quoteVolume: Double,   // quote volume
)

interface MarketRepository {
    suspend fun getPairs(): List<MarketPair>
    suspend fun getPairDetail(symbol: String): PairDetail
}
```

**Symbol → base/quote parsing:** strip a known quote suffix. For Phase 1 we only surface
USDT pairs, so: if symbol ends with `USDT`, `quoteAsset = "USDT"`, `baseAsset = symbol
without suffix`. A small `parseSymbol(symbol): Pair<String, String>` helper in `core:data`
(extensible to BTC/BNB/etc. quotes later).

### Data layer (`core:data`)

- Replace `CoinGeckoApi` → `BinanceApi` (Retrofit interface, two endpoints above).
- New DTO `Ticker24hrDto` (all-string numerics) + mappers `toMarketPair()`,
  `toPairDetail()`.
- Delete `CoinDto`, `CoinDetailDto` and their nested types (CoinImage/Description/etc.).
- `CoinRepositoryImpl` → `MarketRepositoryImpl`:
  - `getPairs()`: fetch all 24hr tickers → filter `symbol.endsWith("USDT")` → sort by
    `quoteVolume` desc → take top 20 → map.
  - `getPairDetail(symbol)`: fetch single ticker → map.
- DI: `RepositoryModule`/`DataModule` bind `MarketRepository`.

### Network (`core:network`)

- `NetworkModule`: change `baseUrl` to `https://api.binance.com/api/v3/`. Update the
  doc comment (no more CoinGecko reference).

### UI touch-ups (keep compiling + green)

- `feature:coinlist` — `CoinCell` shows `baseAsset` + quote (e.g. "BTC/USDT") as title,
  `lastPrice` formatted, `priceChangePercent` colored. Remove `AsyncImage` (no logo).
  Rename screen/VM/state conceptually (`CoinList*` may stay named or rename to
  `MarketList*` — Claude will rename for clarity).
- `feature:coindetail` — remove logo header, market-cap/ATH tiles, and About/description
  section. Show: pair title, last price, 24h change pill, 24h high / 24h low / 24h volume /
  quote volume tiles. (Chart, order book, buy/sell come in later phases.)
- Tests: update `CoinRepositoryImplTest`, `CoinListViewModelTest`, `CoinTestFixtures`,
  `FakeCoinGeckoApi` (→ `FakeBinanceApi`), `FakeCoinRepository` to the new model.

### Phase 1 acceptance

- `./gradlew assembleDebug` succeeds.
- `./gradlew test` green.
- App launches, list shows top-20 USDT pairs by volume from Binance, tapping a pair shows
  its 24h detail.

---

## Phase 2 — Detail header + candlestick chart (Prateek)

### Endpoint

`GET /klines?symbol=BTCUSDT&interval=1h&limit=100` → array of arrays. Each candle:
`[openTime, open, high, low, close, volume, closeTime, ...]` (strings for OHLCV).

```kotlin
data class Candle(
    val openTime: Long,
    val open: Double, val high: Double, val low: Double, val close: Double,
    val volume: Double,
)
```

Add `getKlines(symbol, interval, limit): List<Candle>` to `MarketRepository`.
DTO is a `List<List<String>>` (or `List<Any>`); map by index.

### Chart

- Custom Compose `Canvas` candlestick renderer (no third-party dependency).
- Map price range → vertical pixels, candle index → horizontal pixels.
- Each candle: a thin wick line (high→low) + a body rect (open→close); green if
  `close >= open`, red otherwise (reuse `Green`/`Red` from designsystem).
- Interval selector row: `1h / 4h / 1d / 1w` (chips); changing it re-fetches klines.

### Layout

Detail screen, top to bottom: price header (last price + 24h change pill) → interval
chips → candlestick chart → 24h stat tiles → **Buy / Sell** buttons (navigate to order
page with `symbol`, side).

---

## Phase 3 — Order book + WebSocket (hybrid) (Prateek)

### Strategy (hybrid)

- **WebSocket** for fast-moving data: live price + order book.
- **REST** for klines (Phase 2) and the initial 24h stats (Phase 1).

### WebSocket (`core:network`)

- OkHttp `WebSocket` + `WebSocketListener`. Provide a factory/helper in `core:network`
  that opens a stream by URL and exposes messages as a `Flow<String>` (or typed events).
- Reconnect with backoff on failure/close; tie lifecycle to the screen (collect in VM's
  `viewModelScope`, cancel on clear).

### Streams

- Order book: `wss://stream.binance.com:9443/ws/<symbol-lower>@depth20@100ms`
  → top 20 bids/asks, pushed every 100ms (no manual snapshot+diff needed at depth20).
- Live price: `<symbol-lower>@ticker` (24h rolling) or `@trade` (per-trade) — pick
  `@ticker` to refresh the header price + 24h change without REST polling.

### UI

- Red/green order-book ladder: asks (red) descending above, bids (green) ascending below,
  each row = price + quantity, optional depth bar. Mid spread in the center.

---

## Phase 4/5 — Order placement, Testnet (Prateek)

### Decision

Order placement targets **Binance Testnet** directly (real signed requests, fake money).
A `SimulatedOrderRepository` is **not** built; the `OrderRepository` interface still exists
as the seam so a simulated impl could be added later.

### New module `feature:order`

UI (Binance-style order form):
- Buy / Sell segmented toggle (side).
- Limit / Market type toggle.
- Inputs: price (limit only), amount (base asset), computed total (quote).
- Percentage slider (25/50/75/100%) of available (testnet) balance.
- Confirm button → places order → success/error result surfaced.

### Domain (`core:domain`)

```kotlin
enum class OrderSide { BUY, SELL }
enum class OrderType { LIMIT, MARKET }

data class OrderRequest(
    val symbol: String, val side: OrderSide, val type: OrderType,
    val quantity: Double, val price: Double?,   // price null for MARKET
)
data class OrderResult(
    val orderId: Long, val status: String,
    val executedQty: Double, val symbol: String,
)

interface OrderRepository {
    suspend fun placeOrder(request: OrderRequest): OrderResult
    suspend fun getBalances(): List<Balance>   // for the % slider
}
data class Balance(val asset: String, val free: Double, val locked: Double)
```

### Testnet trading (`core:data`)

- Base URL `https://testnet.binance.vision/api/v3/`.
- Signed endpoints: `POST /order`, `GET /account` (balances).
- **Signing:** every signed request includes `timestamp` (ms) and a `signature` =
  HMAC-SHA256 of the query string, key = API **secret**. API **key** sent in header
  `X-MBX-APIKEY`. A `BinanceSigner` util in `core:data` builds the signed query.
- **Key storage:** testnet apiKey + secret kept out of source control. Options (decide at
  impl): `local.properties` → `BuildConfig` fields (simplest for a learning app), or
  EncryptedSharedPreferences with an in-app settings screen. Recommended: `local.properties`
  + `BuildConfig` to start.
- A separate signed OkHttp client / Retrofit instance (different base URL + apiKey header
  interceptor) — keep it isolated from the public market-data client.

### Acceptance

- Enter amount/price for a pair, confirm, and a real order appears on Binance Testnet;
  result (orderId/status) shown in the app. Balances drive the % slider.

---

## Testing strategy

- Repository tests with fake APIs (as today): `FakeBinanceApi` returns canned ticker/kline
  JSON-shaped DTOs; assert mapping + filter/sort.
- ViewModel tests with `FakeMarketRepository` + Turbine (already a dependency).
- Order signing: unit-test `BinanceSigner` against a known query string / signature vector.
- Chart/order-book composables: Compose previews for visual checks; pure mapping logic
  (price→pixel) extracted and unit-tested where practical.

## Out of scope (YAGNI)

- Keeping CoinGecko for logos/market-cap (could be added later as a static metadata source).
- Non-USDT quote pairs in the list (parser is extensible but list is USDT-only for now).
- Live (real-money) Binance trading.
- Candle WebSocket streaming (klines stay REST).
