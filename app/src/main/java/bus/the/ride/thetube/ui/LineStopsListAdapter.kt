package bus.the.ride.thetube.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import bus.the.ride.thetube.R
import bus.the.ride.thetube.models.Stop

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
            if (isSelectedStop(position)) {
                stopNamePrimary.visibility = VISIBLE
                stopNameSecondary.visibility = GONE
                stopNamePrimary.text = stops[position].name
            } else {
                stopNamePrimary.visibility = GONE
                stopNameSecondary.visibility = VISIBLE
                stopNameSecondary.text = stops[position].name
            }
        }
    }

    fun isSelectedStop(position:Int) = stops[position].id == selectedStopId

    fun bind(data: List<Stop>, stopId: String) {
        selectedStopId = stopId
        stops.clear()
        stops.addAll(data)
        notifyDataSetChanged()
    }

    class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val stopNamePrimary: TextView = root.findViewById(R.id.stopNamePrimary)
        val stopNameSecondary: TextView = root.findViewById(R.id.stopNameSecondary)
    }
}