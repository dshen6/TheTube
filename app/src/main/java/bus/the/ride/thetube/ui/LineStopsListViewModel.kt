package bus.the.ride.thetube.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import bus.the.ride.thetube.ViewState
import bus.the.ride.thetube.api.TubeApi
import bus.the.ride.thetube.models.LineStopsAndCursor
import bus.the.ride.thetube.models.Stop
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by Shen on 2/5/2018.
 */
class LineStopsListViewModel : ViewModel() {

    fun getDataForLineId(lineId: String, stationId: String) = LineStopsListLiveData(lineId, stationId)

    class LineStopsListLiveData(lineId: String, private val stationId: String) : LiveData<ViewState<LineStopsAndCursor>>() {

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
                    .subscribe({ stops ->
                        if (stops.isEmpty()) {
                            value = ViewState.Empty()
                        } else {
                            val currentStationIndex = stops.indexOfFirst { it.id == stationId }
                            value = ViewState.DataReady(LineStopsAndCursor(stops, stationId, currentStationIndex))
                            loadCursorData(stops)
                        }
                    }, {
                        value = ViewState.Error()
                    })
        }

        private fun loadCursorData(stops: List<Stop>) {
            TubeApi.instance.getClosestStationInLineFromLocation(stationId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe( {station ->
                        val stationIndex = stops.indexOfFirst { it.id == station.stationId }
                        value = ViewState.DataReady(LineStopsAndCursor(stops, stationId, stationIndex, 0.0))
                    }, {
                        // ignored
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