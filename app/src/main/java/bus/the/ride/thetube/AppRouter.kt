package bus.the.ride.thetube

import android.support.v4.app.FragmentActivity
import bus.the.ride.thetube.models.StationInRadius

/**
 * Created by Shen on 2/4/2018.
 */
class AppRouter {
    companion object {

        private const val DEFAULT_FRAGMENT_CONTAINER_ID = R.id.main_fragment

        fun showNearbyStationListFragment(activity: FragmentActivity) {
            val fragment = NearbyStationListFragment()
            activity.supportFragmentManager
                    .beginTransaction()
                    .replace(DEFAULT_FRAGMENT_CONTAINER_ID, fragment)
                    .commit()
        }

        fun showLineStopsListFragment(activity: FragmentActivity, lineId: String, station: StationInRadius) {
            val fragment = NearbyStationListFragment()
            activity.supportFragmentManager
                    .beginTransaction()
                    .replace(DEFAULT_FRAGMENT_CONTAINER_ID, fragment)
                    .addToBackStack(null)
                    .commit()
        }
    }
}