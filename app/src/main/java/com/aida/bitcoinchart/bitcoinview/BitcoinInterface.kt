package com.aida.bitcoinchart.bitcoinview

import com.github.mikephil.charting.data.Entry

data class BitcoinViewState(
    val isLoading: Boolean,
    val data: ArrayList<Entry>?,
    val error: Throwable?
)

sealed class BitcoinViewIntent {
    //user intents
    data class DaysButtonClicked(val timespan: String) : BitcoinViewIntent()

    //data intent
    data class ValuesReceived(val list: ArrayList<Entry>) : BitcoinViewIntent()
    data class Error(val throwable: Throwable) : BitcoinViewIntent()
}