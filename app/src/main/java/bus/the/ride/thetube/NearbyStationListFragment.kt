package bus.the.ride.thetube

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bus.the.ride.thetube.models.StationInRadius
import bus.the.ride.thetube.ui.NearbyStationListViewDelegate
import bus.the.ride.thetube.ui.NearbyStationListViewModel
import bus.the.ride.thetube.util.ArrivalItemClickRunnable
import bus.the.ride.thetube.util.ViewModelProviderHelper
import io.reactivex.functions.Consumer

/**
 * Created by Shen on 2/4/2018.
 */
class NearbyStationListFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProviderHelper.forFragment(this).get(NearbyStationListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val nearbyStationListViewDelegate = NearbyStationListViewDelegate.create(inflater, container)
        nearbyStationListViewDelegate.arrivalItemClickRunnable = object : ArrivalItemClickRunnable {
            override fun run(lineId: String, station: StationInRadius) {
                AppRouter.showLineStopsListFragment(activity, lineId, station)
            }
        }

        viewModel.observe(Consumer { viewState ->
            nearbyStationListViewDelegate.setState(viewState)
        })
        return nearbyStationListViewDelegate.root
    }

}