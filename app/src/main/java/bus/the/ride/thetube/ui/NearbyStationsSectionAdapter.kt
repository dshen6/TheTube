package bus.the.ride.thetube.ui

import android.content.Context
import bus.the.ride.thetube.NearbyStationsAndArrivals

/**
 * Created by Shen on 2/3/2018.
 */
class NearbyStationsSectionAdapter(private val context: Context) : SectionAdapter() {

    fun bind(data: NearbyStationsAndArrivals) {
        removeAllSections()
        data.forEach { (station, predictions) ->
            addSection(station.name, StationAndArrivalsSection(context, station to predictions))
        }
        notifyDataSetChanged()
    }
}