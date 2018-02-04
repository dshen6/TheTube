package bus.the.ride.thetube.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Shen on 1/20/2018.
 */
data class StationsInRadiusResponse(@SerializedName("stopPoints") val stations: List<StationInRadius>)