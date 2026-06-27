package com.techieblossom.cryptotracker.data.di

import com.techieblossom.cryptotracker.data.remote.BinanceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Creates the Binance API from the Retrofit instance provided by core:network. This lives in
 * core:data (not core:network) because the API interface + its DTOs are data-layer concerns.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideBinanceApi(retrofit: Retrofit): BinanceApi =
        retrofit.create(BinanceApi::class.java)
}
