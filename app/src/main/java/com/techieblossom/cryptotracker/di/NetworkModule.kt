package com.techieblossom.cryptotracker.di

import com.squareup.moshi.Moshi
import com.techieblossom.cryptotracker.data.remote.CoinGeckoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

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
            baseUrl("https://api.coingecko.com/api/v3/")
            client(okHttpClient)
            addConverterFactory(MoshiConverterFactory.create(moshi))
        }.build()

    @Provides
    @Singleton
    fun provideCoinGeckoApi(retrofit: Retrofit): CoinGeckoApi =
        retrofit.create(CoinGeckoApi::class.java)

}