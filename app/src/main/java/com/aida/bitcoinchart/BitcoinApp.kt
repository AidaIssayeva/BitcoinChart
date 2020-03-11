package com.aida.bitcoinchart

import android.app.Application
import com.aida.bitcoinchart.dagger.AppModule
import com.aida.bitcoinchart.dagger.DaggerAppComponent

class BitcoinApp : Application() {

    val component by lazy {
        module = AppModule()
        DaggerAppComponent.builder().appModule(module).build()
    }
    private lateinit var module: AppModule
}