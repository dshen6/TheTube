package bus.the.ride.thetube.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Shen on 2/5/2018.
 */
data class StopLatLonResponse(@SerializedName("lat") val lat: Double, @SerializedName("lon") val lon: Double)