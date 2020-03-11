package com.aida.bitcoinchart.bitcoinview

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.aida.bitcoinchart.dagger.AppComponent
import com.github.mikephil.charting.data.Entry
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable
import javax.inject.Inject

class BitcoinViewModel @Inject constructor(private val bitcoinRepository: BitcoinRepository) :
    ViewModel() {

    val disposables = CompositeDisposable()

    class Factory(private val component: AppComponent) : ViewModelProvider.Factory {
        override fun <T : android.arch.lifecycle.ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BitcoinViewModel::class.java)) {
                return component.viewModel() as T
            } else {
                throw RuntimeException("Wrong ViewModel Factory for ${modelClass.simpleName}")
            }
        }
    }

    private val _state = MutableLiveData<BitcoinViewState>()
    val state: LiveData<BitcoinViewState> = _state


    fun getBitcoinPrice(timeSpan: String) =
        bitcoinRepository.getBitcoinPrice(timeSpan).subscribeOn(Schedulers.io())
            .map {
                val list = arrayListOf<Entry>()
                it.values.forEach {
                    list.add(Entry(it.x.toFloat(), it.y.toFloat()))
                }
                return@map list
            }
            .observeOn(AndroidSchedulers.mainThread())


    fun bind(intents: Observable<BitcoinViewIntent>) {
        val obs = bindIntents(intents)
            .scanWith(initialStateCallable, reducer)
            .distinctUntilChanged()

        disposables += obs.subscribe(
            { viewState -> _state.value = viewState },
            { throw IllegalStateException("View state obs should never error") },
            { throw IllegalStateException("View state obs never complete") })
    }

    private fun bindIntents(viewIntent: Observable<BitcoinViewIntent>): Observable<BitcoinViewIntent> {
        val dataObservable = getBitcoinPrice("1year")
            .map<BitcoinViewIntent> {
                BitcoinViewIntent.ValuesReceived(it)
            }.onErrorReturn {
                BitcoinViewIntent.Error(it)
            }
            .toObservable()

        val viewObservables = viewIntent.publish { intents ->
            val buttonClicked = viewIntent.ofType(BitcoinViewIntent.DaysButtonClicked::class.java)
                .flatMap {
                    getBitcoinPrice(it.timespan)
                        .map<BitcoinViewIntent> {
                            BitcoinViewIntent.ValuesReceived(it)
                        }.onErrorReturn {
                            BitcoinViewIntent.Error(it)
                        }
                        .toObservable()
                }
            return@publish buttonClicked
        }
        return Observable.mergeArray(viewObservables, dataObservable)
    }

    private val initialStateCallable = Callable {
        BitcoinViewState(
            isLoading = true,
            data = null,
            error = null
        )
    }
    private val reducer =
        BiFunction<BitcoinViewState, BitcoinViewIntent, BitcoinViewState> { previous, intent ->
            when (intent) {
                is BitcoinViewIntent.DaysButtonClicked -> {
                    previous.copy(
                        isLoading = true,
                        data = null
                    )
                }
                is BitcoinViewIntent.ValuesReceived -> {
                    previous.copy(
                        isLoading = false,
                        data = intent.list
                    )
                }
                is BitcoinViewIntent.Error -> {
                    previous.copy(
                        isLoading = false,
                        data = null,
                        error = intent.throwable

                    )
                }
            }
        }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}