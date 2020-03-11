package com.aida.bitcoinchart.bitcoinview

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.View
import com.aida.bitcoinchart.BitcoinApp
import com.aida.bitcoinchart.R
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat


class MainActivity : FragmentActivity() {
    private val intents = PublishSubject.create<BitcoinViewIntent>()

    private lateinit var viewModel: BitcoinViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val factory =
            BitcoinViewModel.Factory((application as BitcoinApp).component)
        viewModel = ViewModelProviders.of(this, factory).get(BitcoinViewModel::class.java)

        viewModel.apply {
            bind(intents.hide())
        }

        setChartView()

        button_30days.setOnClickListener {
            intents.onNext(BitcoinViewIntent.DaysButtonClicked("30days"))
        }
        button_180days.setOnClickListener {
            intents.onNext(BitcoinViewIntent.DaysButtonClicked("180days"))
        }
        button_1year.setOnClickListener {
            intents.onNext(BitcoinViewIntent.DaysButtonClicked("1year"))
        }
        button_60days.setOnClickListener {
            intents.onNext(BitcoinViewIntent.DaysButtonClicked("60days"))
        }

        viewModel.state.observe(this, android.arch.lifecycle.Observer {
            if (it != null) render(it)
        })
    }

    private fun render(viewState: BitcoinViewState) {
        if (!viewState.data.isNullOrEmpty()) {
            setChartData(viewState.data)
        } else {
            lineChart.setVisibleOrGone(false)
        }
        progress_bar.setVisibleOrGone(viewState.isLoading)
        button_30days.isEnabled = !viewState.isLoading
        button_180days.isEnabled = !viewState.isLoading
        button_1year.isEnabled = !viewState.isLoading
        button_60days.isEnabled = !viewState.isLoading
        if (viewState.error != null) {
            tv_title.text = getString(R.string.error_title, viewState.error.message)
        }
    }

    private fun setChartData(entryList: ArrayList<Entry>) {
        lineChart.clearValues()
        lineChart.setVisibleOrGone(true)
        val set1 = LineDataSet(entryList, getString(R.string.bitcoin_chart))
        lineChart.data.addDataSet(set1)
        lineChart.data.notifyDataChanged()
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    }


    private fun setChartView() {
        val xAxis = lineChart.xAxis
        xAxis.granularity = 1f
        val simpleDateFormat = SimpleDateFormat("yy/MM/dd")
        xAxis.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return simpleDateFormat.format(value.toLong() * 1000)
            }
        })
        lineChart.data = LineData()
        lineChart.setTouchEnabled(true)
    }
}

fun View.setVisibleOrGone(isVisible: Boolean) {
    visibility = when (isVisible) {
        true -> View.VISIBLE
        else -> View.GONE
    }
}