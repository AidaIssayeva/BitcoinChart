package com.aida.bitcoinchart.network

import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

data class BitcoinResponse(
    val name: String,
    val description: String,
    val period: String,
    val unit: String,
    val status: String,
    val values: List<BitcoinPricePerTimestamp>
)

data class BitcoinPricePerTimestamp(
    val x: Long,
    val y: BigDecimal
)

data class UiModel(
    val x: String,
    val y: BigDecimal
)

fun BitcoinPricePerTimestamp.toUiModel(): UiModel {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    val date = Date(this.x * 1000)
    return UiModel(
        simpleDateFormat.format(date),
        this.y
    )
}