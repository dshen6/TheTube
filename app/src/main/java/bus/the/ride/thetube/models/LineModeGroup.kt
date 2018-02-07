package bus.the.ride.thetube.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Shen on 2/7/2018.
 */
data class LineModeGroup(@SerializedName("modeName") val modeName :String , @SerializedName("lineIdentifier") val lineIds: List<String>)