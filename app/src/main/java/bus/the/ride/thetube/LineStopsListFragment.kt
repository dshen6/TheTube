package bus.the.ride.thetube

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bus.the.ride.thetube.api.TubeApi
import bus.the.ride.thetube.ui.LineStopsListViewDelegate
import bus.the.ride.thetube.ui.LineStopsListViewModel
import bus.the.ride.thetube.ui.NearbyStationListViewModel
import bus.the.ride.thetube.util.IntentExtras
import bus.the.ride.thetube.util.NullableUtils.ifNotNull
import bus.the.ride.thetube.util.ViewModelProviderHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Shen on 2/4/2018.
 */
class LineStopsListFragment : Fragment() {

    var lineId: String? = null
    var stationId: String? = null

    private val viewModel by lazy {
        ViewModelProviderHelper.forFragment(this).get(LineStopsListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.apply {
            lineId = getString(IntentExtras.LINE_ID_STRING)
            stationId = getString(IntentExtras.STATION_ID_STRING)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val lineStopsListViewDelegate = LineStopsListViewDelegate.create(inflater, container)
        ifNotNull(lineId, stationId) { lineId, stationId ->
            viewModel.getDataForLineId(lineId, stationId).observe(this, Observer { viewState ->
                viewState?.let { lineStopsListViewDelegate.setState(it) }
            })
        }

        return lineStopsListViewDelegate.root
    }
}