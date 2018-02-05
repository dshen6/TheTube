package bus.the.ride.thetube.ui

import android.content.Context
import android.content.res.Resources
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import bus.the.ride.thetube.R
import bus.the.ride.thetube.models.ArrivalPrediction
import bus.the.ride.thetube.models.StationInRadius
import bus.the.ride.thetube.util.TimeFormatUtil

/**
 * Created by Shen on 2/4/2018.
 */
class StationAndArrivalsSection(private val context: Context, private val stationAndArrivals: Pair<StationInRadius, List<ArrivalPrediction>>) : Section(R.layout.header_item_nearby_station, null, R.layout.item_arrival) {

    companion object {
        private const val ARRIVALS_PER_STATION = 3
    }

    override fun getContentItemsTotal() = Math.min(stationAndArrivals.second.size, ARRIVALS_PER_STATION)

    override fun onCreateHeaderViewHolder(view: View) = HeaderViewHolder(view)

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?) {
        (holder as HeaderViewHolder).apply {
            stationName.text = stationAndArrivals.first.name
            val meters = stationAndArrivals.first.distanceMeters.toInt()
            distanceMeters.text = context.resources.getQuantityString(R.plurals.meters, meters, meters)
        }
    }

    override fun onCreateFooterViewHolder(view: View?): RecyclerView.ViewHolder {
        throw NotImplementedError("footer not implemented")
    }

    override fun onBindFooterViewHolder(holder: RecyclerView.ViewHolder?) {
    }

    override fun onCreateItemViewHolder(view: View) = ContentViewHolder(view)

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as ContentViewHolder).apply {
            if (position < stationAndArrivals.second.size) {
                arrivalsAndTimes.text = formatArrivalsAndTimes(stationAndArrivals.second[position], context.resources)
            }
        }
    }

    private fun formatArrivalsAndTimes(prediction: ArrivalPrediction, res: Resources): String {
        return res.getString(R.string.train_to_x_in, prediction.lineName, prediction.towardsStation, TimeFormatUtil.secondsToArrivalTime(prediction.timeToArrival, res))
    }

    class HeaderViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val stationName: TextView = root.findViewById(R.id.station_name)
        val distanceMeters: TextView = root.findViewById(R.id.distance_meters)
    }

    class ContentViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val arrivalsAndTimes: TextView = root.findViewById(R.id.arrivals_and_times)
    }

}