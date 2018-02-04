package bus.the.ride.thetube.util

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelStores
import android.support.v4.app.Fragment

/**
 * Created by Shen on 2/4/2018.
 */
class ViewModelProviderHelper {
    companion object {

        fun forFragment(fragment: Fragment):ViewModelProvider {
            val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(fragment.activity.application)
            val store = ViewModelStores.of(fragment.activity)
            return ViewModelProvider(store, factory)
        }
    }
}