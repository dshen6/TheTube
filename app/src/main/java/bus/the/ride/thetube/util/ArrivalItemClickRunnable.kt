package bus.the.ride.thetube.util

import bus.the.ride.thetube.models.StationInRadius

/**
 * Created by Shen on 2/4/2018.
 */
interface ArrivalItemClickRunnable {
    fun run(lineId: String, station: StationInRadius)
}