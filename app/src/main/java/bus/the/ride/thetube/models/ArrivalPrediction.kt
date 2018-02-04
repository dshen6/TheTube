package bus.the.ride.thetube.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Shen on 1/20/2018.
 */
data class ArrivalPrediction(@SerializedName("lineName") val lineName: String, @SerializedName("lineId") val lineId: String,@SerializedName("towards") val towardsStation: String, @SerializedName("timeToStation") val timeToArrival: Int)