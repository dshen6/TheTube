package bus.the.ride.thetube.util

import android.content.res.Resources
import android.util.TypedValue
import android.view.View.GONE
import android.view.View.VISIBLE

/**
 * Created by Shen on 2/5/2018.
 */

fun Boolean.asVisibility() : Int = if (this) { VISIBLE } else { GONE }

fun Float.dpToPixels() : Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics).toInt()