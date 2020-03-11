package com.aida.bitcoinchart.dagger

import com.aida.bitcoinchart.bitcoinview.BitcoinViewModel
import com.aida.bitcoinchart.network.NetworkApi
import dagger.Component
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    @Named("BitcoinRetrofit")
    fun bitcoinRetrofit(): Retrofit

    fun networkApi(): NetworkApi

    fun viewModel(): BitcoinViewModel
}