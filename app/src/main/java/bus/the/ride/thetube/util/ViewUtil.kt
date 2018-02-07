package bus.the.ride.thetube.util

import android.content.res.Resources
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.View.GONE
import android.view.View.VISIBLE

/**
 * Created by Shen on 2/5/2018.
 */

fun Boolean.asVisibility() : Int = if (this) { VISIBLE } else { GONE }

fun Float.dpToPixels() : Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics).toInt()

fun Fragment.setPageTitle(pageTitle: String) {
    val actionBar = (activity as AppCompatActivity).supportActionBar
    val title = actionBar?.title
    if (title != pageTitle) {
        actionBar?.title = pageTitle
    }
}