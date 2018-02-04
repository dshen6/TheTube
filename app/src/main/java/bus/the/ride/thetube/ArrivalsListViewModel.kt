package bus.the.ride.thetube

import android.arch.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by Shen on 1/31/2018.
 */
class ArrivalsListViewModel internal constructor(private val viewStateSubject: BehaviorSubject<ViewState<NearbyStationsAndArrivals>> = BehaviorSubject.createDefault(ViewState.Init()),
                                                 private val modelSubject: BehaviorSubject<NearbyStationsAndArrivals> = TubeApi.instance.getStationsAndArrivalsSubject(),
                                                 private val compositeDisposable: CompositeDisposable = CompositeDisposable()) : ViewModel() {

    fun observe(consumer: Consumer<ViewState<NearbyStationsAndArrivals>>) {
        addDisposable(viewStateSubject.subscribe(consumer))
        loadData()
    }

    private fun loadData() {
        if (modelSubject.hasObservers()) {
            return
        }
        addDisposable(modelSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    if (viewStateSubject.value is ViewState.Init) {
                        publishState(ViewState.Loading())
                    }
                }
                .subscribe({
                    if (it.isEmpty()) {
                        publishState(ViewState.Empty())
                    } else {
                        publishState(ViewState.DataReady(it))
                    }
                }, {
                    publishState(ViewState.Error())
                }))
    }

    private fun publishState(state: ViewState<NearbyStationsAndArrivals>) {
        viewStateSubject.onNext(state)
    }

    private fun addDisposable(disposable: Disposable) {
        compositeDisposable += disposable
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}