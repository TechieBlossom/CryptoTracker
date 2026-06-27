package com.techieblossom.cryptotracker.ui.format

import android.icu.text.CompactDecimalFormat
import java.text.NumberFormat
import java.util.Locale

private val LOCALE: Locale = Locale.US

fun Double?.formatAsUsd(): String =
    this?.let { NumberFormat.getCurrencyInstance(LOCALE).format(this) } ?: "—"

fun Double?.formatAsPercent(): String =
    this?.let { String.format(LOCALE, "%+.2f%%", it) } ?: "—"

fun Double?.formatAsCompactUsd(): String =
    this?.let { CompactDecimalFormat.getCurrencyInstance(LOCALE).format(this) } ?: "—"