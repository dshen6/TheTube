package bus.the.ride.thetube.ui

import android.content.res.Resources
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import bus.the.ride.thetube.NearbyStationsAndArrivals
import bus.the.ride.thetube.R
import bus.the.ride.thetube.models.ArrivalPrediction
import bus.the.ride.thetube.util.TimeFormatUtil

/**
 * Created by Shen on 2/3/2018.
 */
class NearbyStationListAdapter(private val arrivals: NearbyStationsAndArrivals) : RecyclerView.Adapter<NearbyStationListAdapter.ViewHolder>() {

    companion object {
        private const val ARRIVALS_PER_STATION = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_nearby_station, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = arrivals.size

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.apply {
            arrivals[position].let {(station, predictions) ->
                stationName.text = station.name
                arrivalsAndTimes.text = formatArrivalsAndTimes(predictions, arrivalsAndTimes.resources)
            }
        }
    }

    fun bind(data: NearbyStationsAndArrivals) {
        arrivals.clear()
        arrivals.addAll(data)
        notifyDataSetChanged()
    }

    private fun formatArrivalsAndTimes(predictions: List<ArrivalPrediction>, res: Resources): String {
        return StringBuilder().apply {
            predictions.forEachIndexed {index, prediction ->
                if (index < ARRIVALS_PER_STATION) {
                    append(res.getString(R.string.train_to_x_in, prediction.lineName, prediction.towardsStation, TimeFormatUtil.secondsToArrivalTime(prediction.timeToArrival, res)) + "\n")
                }
            }
        }.toString()
    }

    class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val stationName: TextView = root.findViewById(R.id.station_name)
        val arrivalsAndTimes: TextView = root.findViewById(R.id.arrivals_and_times)
    }
}