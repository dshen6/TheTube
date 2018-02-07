package bus.the.ride.thetube.util

import bus.the.ride.thetube.models.StopLatLonResponse
import java.lang.Math.*

/**
 * Created by Shen on 2/5/2018.
 */
object Haversine {

    const val R = 6372.8 // in kilometers

    fun computeDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val λ1 = toRadians(lat1)
        val λ2 = toRadians(lat2)
        val Δλ = toRadians(lat2 - lat1)
        val Δφ = toRadians(lon2 - lon1)
        return 2 * R * asin(sqrt(pow(sin(Δλ / 2), 2.0) + pow(sin(Δφ / 2), 2.0) * cos(λ1) * cos(λ2)))
    }

}