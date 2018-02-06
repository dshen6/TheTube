package bus.the.ride.thetube.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import bus.the.ride.thetube.ViewState
import bus.the.ride.thetube.api.TubeApi
import bus.the.ride.thetube.models.Stop
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by Shen on 2/5/2018.
 */
class LineStopsListViewModel : ViewModel() {

    fun getDataForLineId(lineId: String) = LineStopsListLiveData(lineId)

    class LineStopsListLiveData(lineId: String) : LiveData<ViewState<List<Stop>>>() {

        private val request: Single<List<Stop>> = TubeApi.instance.getStopsForLine(lineId)

        init {
            value = ViewState.Init()
            loadData()
        }

        private var disposable: Disposable? = null

        private fun loadData() {
            disposable = request.observeOn(AndroidSchedulers.mainThread())
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
            if (disposable == null) {
                loadData()
            }
        }

        override fun onInactive() {
            disposable?.dispose()
            disposable = null
        }
    }
}