package bus.the.ride.thetube.api

import bus.the.ride.thetube.models.*
import bus.the.ride.thetube.util.Haversine
import bus.the.ride.thetube.util.exponentialBackoff
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by Shen on 1/20/2018.
 */
typealias NearbyStationsAndArrivals = MutableList<Pair<StationInRadius, List<ArrivalPrediction>>>

class TubeApi private constructor(private val tubeService: TubeService) {

    companion object {

        private const val TUBE_STATION_STOP_TYPE = "NaptanMetroStation"
        private const val DEFAULT_RADIUS = 1000
        private const val DEFAULT_LATITUDE = 51.503147F // Waterloo station
        private const val DEFAULT_LONGITUDE = -0.113245F

        val instance: TubeApi by lazy { TubeApi(RetrofitManager.instance.create(TubeService::class.java)) }
    }

    private interface TubeService {
        //https://api.tfl.gov.uk/StopPoint?lat=51.49454&lon=-0.100601&stopTypes=NaptanMetroStation&radius=800&app_id={{app_id}}&app_key={{app_key}}
        @GET("StopPoint")
        fun getStationsInRadiusFromLatLon(@Query("radius") radiusMeters: Int, @Query("lat") latitude: Float, @Query("lon") longitude: Float,
                                          @Query("stopTypes") stopTypes: String = TUBE_STATION_STOP_TYPE): Single<StationsInRadiusResponse>

        // https://api.tfl.gov.uk/StopPoint/940GZZLUASL/Arrivals?app_id={{app_id}}&app_key={{app_key}}
        @GET("StopPoint/{stationAtcoCode}/Arrivals")
        fun getArrivalsAtStation(@Path("stationAtcoCode") stationId: String): Single<List<ArrivalPrediction>>

        // https://api.tfl.gov.uk/Line/bakerloo/StopPoints?app_id={{app_id}}&app_key={{app_key}}
        @GET("Line/{lineId}/StopPoints")
        fun getStopsForLine(@Path("lineId") lineId: String): Single<List<Stop>>

        // https://api.tfl.gov.uk/StopPoint/940GZZLUASL?app_id={{app_id}}&app_key={{app_key}}
        @GET("StopPoint/{stopId}")
        fun getStopLatLon(@Path("stopId") stopId: String): Single<StopLatLonResponse>
    }

    private fun getStationsInRadius(): Single<StationsInRadiusResponse> {
        return tubeService.getStationsInRadiusFromLatLon(DEFAULT_RADIUS, DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
    }

    private fun getArrivalsAtStation(stationId: String): Single<List<ArrivalPrediction>> {
        return tubeService.getArrivalsAtStation(stationId)
    }

    private fun getStationsAndArrivals(): Flowable<Pair<StationInRadius, List<ArrivalPrediction>>> {
        // TODO: Single.merge would be a lot more readable
        return Flowable.create({ emitter ->
            getStationsInRadius().subscribe({
                val outstandingRequestCount = AtomicInteger(it.stations.size)
                it.stations.forEach { station ->
                    getArrivalsAtStation(station.stationId).subscribe({ predictionsList ->
                        emitter.onNext(station to predictionsList.sortedBy { it.timeToArrival })
                        if (outstandingRequestCount.decrementAndGet() == 0) {
                            emitter.onComplete()
                        }
                    }, { emitter.tryOnError(it) })
                }
            }, { emitter.tryOnError(it) })
        }, BackpressureStrategy.LATEST)
    }

    fun getStationsAndArrivalsList(): Single<NearbyStationsAndArrivals> {
        return getStationsAndArrivals()
                .toSortedList { one, two -> one.first.distanceMeters.compareTo(two.first.distanceMeters) }
                .exponentialBackoff(3)
    }

    fun getStopsForLine(lineId: String): Single<List<Stop>> {
        return tubeService.getStopsForLine(lineId)
    }

    fun getClosestDistanceFromTarget(stopIds: List<String>, target: Pair<Double, Double> = (DEFAULT_LATITUDE.toDouble() to DEFAULT_LONGITUDE.toDouble())): Single<Pair<Int, Double>> {
        if (stopIds.isEmpty()) {
            throw IllegalArgumentException("empty stopId list")
        }

        return Single.concat(stopIds.map { stopId ->
            tubeService.getStopLatLon(stopId)
        }).toList().map { Haversine.closestDistanceToTarget(target, it) }
    }
}