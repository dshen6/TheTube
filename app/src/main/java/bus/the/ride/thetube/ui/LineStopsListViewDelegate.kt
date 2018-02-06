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
import bus.the.ride.thetube.models.Stop
import bus.the.ride.thetube.util.asVisibility

/**
 * Created by Shen on 2/4/2018.
 */
class LineStopsListViewDelegate(val root: View, context: Context) {

    companion object {
        fun create(inflater: LayoutInflater, container: ViewGroup?): LineStopsListViewDelegate {
            val root = inflater.inflate(R.layout.fragment_line_stop_list, container, false)
            return LineStopsListViewDelegate(root, inflater.context)
        }
    }

    private val errorText: TextView = root.findViewById(R.id.error_state_text)
    private val progressBar: ProgressBar = root.findViewById(R.id.progressBar)
    private val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView)

    init {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = LineStopsListAdapter(context)
    }

    fun setState(state: ViewState<List<Stop>>) {
        errorText.visibility = (state is ViewState.Error).asVisibility()
        progressBar.visibility = (state is ViewState.Loading).asVisibility()
        recyclerView.visibility = (state is ViewState.DataReady).asVisibility()
        when (state) {
            is ViewState.DataReady -> {
                (recyclerView.adapter as LineStopsListAdapter).bind(state.data, "", 2, .5F)
            }
        }
    }
}