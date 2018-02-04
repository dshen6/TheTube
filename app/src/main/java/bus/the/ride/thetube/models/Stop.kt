package bus.the.ride.thetube.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Shen on 2/4/2018.
 */
data class Stop(@SerializedName("id") val id: String, @SerializedName("commonName") val name: String, @SerializedName("stopType") val stopType: String)