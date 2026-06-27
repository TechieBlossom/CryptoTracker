package com.techieblossom.cryptotracker.core.network

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

/**
 * Provides the shared HTTP stack (OkHttp + Moshi + a configured Retrofit). The concrete API
 * interface (BinanceApi) is created in core:data, which owns the endpoints/DTOs — keeping
 * core:network free of any data-layer knowledge.
 *
 * The Binance base URL lives here for now (single-API app); revisit if a second API is added.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
    }.build()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder().apply {
            baseUrl("https://api.binance.com/api/v3/")
            client(okHttpClient)
            addConverterFactory(MoshiConverterFactory.create(moshi))
        }.build()
}
