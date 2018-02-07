package bus.the.ride.thetube.models

/**
 * Created by Shen on 2/5/2018.
 */
data class LineStopsAndCursor(var stops: List<Stop>, var currentStationId: String? = null, var locationCursorIndex: Int? = null)