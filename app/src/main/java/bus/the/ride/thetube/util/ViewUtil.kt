package bus.the.ride.thetube.util

import android.view.View.GONE
import android.view.View.VISIBLE

/**
 * Created by Shen on 2/5/2018.
 */

fun Boolean.asVisibility() : Int = if (this) { VISIBLE } else { GONE }