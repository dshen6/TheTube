package bus.the.ride.thetube.ui

import android.content.Context
import android.content.res.Resources
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import bus.the.ride.thetube.R
import bus.the.ride.thetube.models.ArrivalPrediction
import bus.the.ride.thetube.models.StationInRadius
import bus.the.ride.thetube.util.ArrivalItemClickRunnable
import bus.the.ride.thetube.util.TimeFormatUtil

/**
 * Created by Shen on 2/4/2018.
 */
class StationAndArrivalsSection(private val context: Context,
                                private val stationAndArrivals: Pair<StationInRadius, List<ArrivalPrediction>>,
                                private val arrivalItemClickRunnable: ArrivalItemClickRunnable?) : Section(R.layout.header_item_nearby_station, null, R.layout.item_arrival) {

    companion object {
        private const val ARRIVALS_PER_STATION = 3
    }

    override fun getContentItemsTotal() = Math.min(Math.max(stationAndArrivals.second.size, 1), ARRIVALS_PER_STATION)

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
            stationAndArrivals.let { (station, predictions) ->
                if (position < predictions.size) {
                    arrivalsAndTimes.text = formatArrivalsAndTimes(predictions[position], context.resources)
                    arrivalItemClickRunnable?.run(predictions[position].lineId, station)
                }
                if (predictions.isEmpty()) {
                    arrivalsAndTimes.text = context.getString(R.string.empty_arrivals_for_station)
                }
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