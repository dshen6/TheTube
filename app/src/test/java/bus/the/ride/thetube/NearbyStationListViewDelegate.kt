package bus.the.ride.thetube

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.ProgressBar
import bus.the.ride.thetube.ui.NearbyStationListViewDelegate
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

/**
 * Created by Shen on 2/6/2018.
 */
@RunWith(RobolectricTestRunner::class)
class NearbyStationListViewDelegate {

    private lateinit var nearbyStationListViewDelegate: NearbyStationListViewDelegate
    private lateinit var root: View

    @Before
    fun init() {
        val activity = Robolectric.setupActivity(MainActivity::class.java)
        root = LayoutInflater.from(activity).inflate(R.layout.fragment_nearby_station_list, FrameLayout(activity), false)
        nearbyStationListViewDelegate = NearbyStationListViewDelegate(root, activity)
    }

    @Test
    fun stateLoading() {
        nearbyStationListViewDelegate.setState(ViewState.Loading())
        assertEquals(VISIBLE, root.findViewById<ProgressBar>(R.id.progressBar).visibility)
        assertEquals(GONE, root.findViewById<View>(R.id.error_state_text).visibility)
        assertEquals(GONE, root.findViewById<View>(R.id.empty_state_text).visibility)
        assertEquals(GONE, root.findViewById<View>(R.id.recyclerView).visibility)
    }

    @Test
    fun stateEmpty() {
        nearbyStationListViewDelegate.setState(ViewState.Empty())
        assertEquals(GONE, root.findViewById<ProgressBar>(R.id.progressBar).visibility)
        assertEquals(GONE, root.findViewById<View>(R.id.error_state_text).visibility)
        assertEquals(VISIBLE, root.findViewById<View>(R.id.empty_state_text).visibility)
        assertEquals(GONE, root.findViewById<View>(R.id.recyclerView).visibility)
    }

    @Test
    fun stateNetworkError() {
        nearbyStationListViewDelegate.setState(ViewState.Error())
        assertEquals(GONE, root.findViewById<ProgressBar>(R.id.progressBar).visibility)
        assertEquals(VISIBLE, root.findViewById<View>(R.id.error_state_text).visibility)
        assertEquals(GONE, root.findViewById<View>(R.id.empty_state_text).visibility)
        assertEquals(GONE, root.findViewById<View>(R.id.recyclerView).visibility)
    }

    @Test
    fun stateDataReady() {
        nearbyStationListViewDelegate.setState(ViewState.DataReady(ArrayList()))
        assertEquals(GONE, root.findViewById<ProgressBar>(R.id.progressBar).visibility)
        assertEquals(GONE, root.findViewById<View>(R.id.error_state_text).visibility)
        assertEquals(GONE, root.findViewById<View>(R.id.empty_state_text).visibility)
        assertEquals(VISIBLE, root.findViewById<View>(R.id.recyclerView).visibility)
        assertEquals(0, root.findViewById<RecyclerView>(R.id.recyclerView).childCount)
    }
}