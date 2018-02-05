package bus.the.ride.thetube.ui

import bus.the.ride.thetube.NearbyStationsAndArrivals

/**
 * Created by Shen on 2/3/2018.
 */
class NearbyStationsSectionAdapter : SectionAdapter() {

    fun bind(data: NearbyStationsAndArrivals) {
        removeAllSections()
        data.forEach { (station, predictions) ->
            if (predictions.isNotEmpty()) {
                addSection(station.name, StationAndArrivalsSection(station to predictions))
            }
        }
        notifyDataSetChanged()
    }
}