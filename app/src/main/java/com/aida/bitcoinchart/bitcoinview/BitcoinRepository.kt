package com.aida.bitcoinchart.bitcoinview

import com.aida.bitcoinchart.network.NetworkApi
import javax.inject.Inject

class BitcoinRepository @Inject constructor(private val networkApi: NetworkApi) {

    fun getBitcoinPrice(timeSpan: String) = networkApi.getBitcoinPrice(timeSpan = timeSpan)

}