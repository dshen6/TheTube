package bus.the.ride.thetube.util

import android.content.res.Resources
import bus.the.ride.thetube.R
import java.util.concurrent.TimeUnit

/**
 * Created by Shen on 2/3/2018.
 */
class TimeFormatUtil {

    companion object {
        fun secondsToArrivalTime(seconds: Int, res: Resources): String {
            return if (seconds < TimeUnit.MINUTES.toSeconds(1)) {
                res.getQuantityString(R.plurals.seconds, seconds, seconds)
            } else {
                val minutes = (seconds / TimeUnit.MINUTES.toSeconds(1)).toInt()
                return res.getQuantityString(R.plurals.minutes, minutes, minutes)
            }
        }
    }
}