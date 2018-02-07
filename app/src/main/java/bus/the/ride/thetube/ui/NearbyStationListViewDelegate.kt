package bus.the.ride.thetube.ui

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import bus.the.ride.thetube.R
import bus.the.ride.thetube.ViewState
import bus.the.ride.thetube.api.NearbyStationsAndArrivals
import bus.the.ride.thetube.util.ArrivalItemClickRunnable
import bus.the.ride.thetube.util.asVisibility

/**
 * Created by Shen on 2/3/2018.
 */
class NearbyStationListViewDelegate(val root: View, private val context: Context) {

    companion object {
        fun create(inflater: LayoutInflater, container: ViewGroup?): NearbyStationListViewDelegate {
            val root = inflater.inflate(R.layout.fragment_nearby_station_list, container, false)
            return NearbyStationListViewDelegate(root, inflater.context)
        }
    }

    private val emptyText: TextView = root.findViewById(R.id.empty_state_text)
    private val errorText: TextView = root.findViewById(R.id.error_state_text)
    private val progressBar: ProgressBar = root.findViewById(R.id.progressBar)
    private val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView)

    init {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = NearbyStationsSectionAdapter(context)
    }

    var arrivalItemClickRunnable: ArrivalItemClickRunnable? = null

    fun setState(state: ViewState<NearbyStationsAndArrivals>) {
        emptyText.visibility = (state is ViewState.Empty).asVisibility()
        errorText.visibility = (state is ViewState.Error).asVisibility()
        progressBar.visibility = (state is ViewState.Loading).asVisibility()
        recyclerView.visibility = (state is ViewState.DataReady).asVisibility()
        when (state) {
            is ViewState.DataReady -> {
                (recyclerView.adapter as NearbyStationsSectionAdapter).bind(state.data, arrivalItemClickRunnable)
            }
        }
    }

}