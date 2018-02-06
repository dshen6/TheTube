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

    class LineStopsListLiveData(lineId: String, val stationId: String) : LiveData<ViewState<LineStopsAndCursor>>() {

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
                            value = ViewState.DataReady(LineStopsAndCursor(stops))
                            loadCursorData(stops)
                        }
                    }, {
                        value = ViewState.Error()
                    })
        }

        private fun loadCursorData(stops: List<Stop>) {
            val currentStationIndex = stops.indexOfFirst { it.id == stationId }
            var addedStopBefore = false
            val surroundingStations = ArrayList<String>().apply {
                if (currentStationIndex > 1) {
                    add(stops[currentStationIndex - 1].id)
                    addedStopBefore = true
                }
                add(stationId)
                if (currentStationIndex < stops.size - 1) {
                    add(stops[currentStationIndex + 1].id)
                }
            }
            TubeApi.instance.getClosestDistanceFromTarget(surroundingStations)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe( {(cursorIndex, cursorOffset) ->
                        val adjustedCursorIndex = cursorIndex + currentStationIndex + if (addedStopBefore) (-1) else (0)
                        value = ViewState.DataReady(LineStopsAndCursor(stops, stationId, adjustedCursorIndex, cursorOffset))
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