package bus.the.ride.thetube.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import bus.the.ride.thetube.ViewState
import bus.the.ride.thetube.api.NearbyStationsAndArrivals
import bus.the.ride.thetube.api.TubeApi
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by Shen on 1/31/2018.
 */
class NearbyStationListViewModel(val data: NearbyStationsLiveData = NearbyStationsLiveData()) : ViewModel() {

    class NearbyStationsLiveData(private val request: Single<NearbyStationsAndArrivals> = TubeApi.instance.getStationsAndArrivalsList()) : LiveData<ViewState<NearbyStationsAndArrivals>>() {

        companion object {
            private const val RETRY_PERIOD_SEC = 30L
        }

        init {
            value = ViewState.Init()
            loadData()
        }

        private var repeatDisposable: Disposable? = null

        private fun loadData() {
            repeatDisposable = request.observeOn(AndroidSchedulers.mainThread())
                    .repeatWhen { completed -> completed.delay(RETRY_PERIOD_SEC, TimeUnit.SECONDS) }
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe {
                        if (value is ViewState.Init) {
                            value = ViewState.Loading()
                        }
                    }
                    .subscribe({
                        value = if (it.isEmpty()) {
                            ViewState.Empty()
                        } else {
                            ViewState.DataReady(it)
                        }
                    }, {
                        value = ViewState.Error()
                    })
        }

        override fun onActive() {
            if (repeatDisposable == null) {
                loadData()
            }
        }

        override fun onInactive() {
            repeatDisposable?.dispose()
            repeatDisposable = null
        }
    }
}