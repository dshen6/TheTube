package bus.the.ride.thetube

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bus.the.ride.thetube.api.TubeApi
import bus.the.ride.thetube.ui.LineStopsListViewDelegate
import bus.the.ride.thetube.util.IntentExtras
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Shen on 2/4/2018.
 */
class LineStopsListFragment : Fragment() {

    var lineId: String? = null
    var stationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.apply {
            lineId = getString(IntentExtras.LINE_ID_STRING)
            stationId = getString(IntentExtras.STATION_ID_STRING)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val lineStopsListViewDelegate = LineStopsListViewDelegate.create(inflater, container)

        lineId?.let {
            TubeApi.instance.getStopsForLine(it)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        lineStopsListViewDelegate.setState(ViewState.DataReady(it))
                    }, {

                    })
        }
        return lineStopsListViewDelegate.root
    }
}