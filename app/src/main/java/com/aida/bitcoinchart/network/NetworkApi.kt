package com.aida.bitcoinchart.network

import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkApi {
    @GET("market-price")
    fun getBitcoinPrice(@Query("timespan") timeSpan: String): Flowable<BitcoinResponse>

}