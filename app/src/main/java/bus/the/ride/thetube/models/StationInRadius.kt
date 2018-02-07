package bus.the.ride.thetube.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Shen on 1/20/2018.
 */
data class StationInRadius(@SerializedName("id") val stationId: String, @SerializedName("commonName") val name : String, @SerializedName("distance") val distanceMeters : Float,
                           @SerializedName("lineModeGroups") val lineModeGroups: List<LineModeGroup>)