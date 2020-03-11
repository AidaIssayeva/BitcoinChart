package com.aida.bitcoinchart.dagger

import com.aida.bitcoinchart.bitcoinview.BitcoinRepository
import com.aida.bitcoinchart.network.NetworkApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    @Named("BitcoinRetrofit")
    fun provideBitcoinRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        client.addInterceptor(loggingInterceptor)
        return Retrofit.Builder()
            .client(client.build())
            .baseUrl("https://api.blockchain.info/charts/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideNetworkApi(@Named("BitcoinRetrofit") retrofit: Retrofit): NetworkApi {
        return retrofit.create(NetworkApi::class.java)
    }

    @Provides
    @Singleton
    fun provideBitcoinRepository(networkApi: NetworkApi): BitcoinRepository {
        return BitcoinRepository(networkApi)
    }
}