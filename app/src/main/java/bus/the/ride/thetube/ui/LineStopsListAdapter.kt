package bus.the.ride.thetube.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import bus.the.ride.thetube.R
import bus.the.ride.thetube.models.LineStopsAndCursor
import bus.the.ride.thetube.models.Stop
import bus.the.ride.thetube.util.asVisibility

/**
 * Created by Shen on 2/4/2018.
 */
class LineStopsListAdapter(private val context: Context, private val stops: MutableList<Stop> = ArrayList(), private var selectedStopId: String? = null) : RecyclerView.Adapter<LineStopsListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stop, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = stops.size

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.apply {
            stopNamePrimary.visibility = isSelectedStop(position).asVisibility()
            stopNameSecondary.visibility = (!isSelectedStop(position)).asVisibility()
            locationCursor.visibility = (currentLocationIndex == position).asVisibility()
            if (isSelectedStop(position)) {
                stopNamePrimary.text = stops[position].name
            } else {
                stopNameSecondary.text = stops[position].name
            }
        }
    }

    private var currentLocationIndex: Int? = null

    private fun isSelectedStop(position: Int) = stops[position].id == selectedStopId

    fun bind(data: LineStopsAndCursor) {
        currentLocationIndex = data.locationCursorIndex
        selectedStopId = data.currentStationId
        stops.clear()
        stops.addAll(data.stops)
        notifyDataSetChanged()
    }

    class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val stopNamePrimary: TextView = root.findViewById(R.id.stopNamePrimary)
        val stopNameSecondary: TextView = root.findViewById(R.id.stopNameSecondary)
        val locationCursor: ImageView = root.findViewById(R.id.locationCursor)
    }
}