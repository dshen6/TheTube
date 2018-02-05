package bus.the.ride.thetube.api

import bus.the.ride.thetube.models.ArrivalPrediction
import bus.the.ride.thetube.models.StationInRadius
import bus.the.ride.thetube.models.StationsInRadiusResponse
import bus.the.ride.thetube.models.Stop
import bus.the.ride.thetube.util.exponentialBackoff
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
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
        private const val RETRY_PERIOD_SEC = 30L

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
    }

    private fun getStationsInRadius(): Single<StationsInRadiusResponse> {
        return tubeService.getStationsInRadiusFromLatLon(DEFAULT_RADIUS, DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
    }

    private fun getArrivalsAtStation(stationId: String): Single<List<ArrivalPrediction>> {
        return tubeService.getArrivalsAtStation(stationId)
    }

    private fun getStationsAndArrivals(): Flowable<Pair<StationInRadius, List<ArrivalPrediction>>> {
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

    fun getStationsAndArrivalsSubject(): BehaviorSubject<NearbyStationsAndArrivals> {
        val subject = BehaviorSubject.create<NearbyStationsAndArrivals>()
        getStationsAndArrivals()
                .toSortedList { one, two -> one.first.distanceMeters.compareTo(two.first.distanceMeters) }
                .exponentialBackoff(3)
                .repeatWhen { completed -> completed.delay(RETRY_PERIOD_SEC, TimeUnit.SECONDS) }
                .subscribe({
                    subject.onNext(it)
                }, {
                    subject.onError(it)
                })
        return subject
    }

    fun getStopsForLine(lineId: String): Single<List<Stop>> {
        return tubeService.getStopsForLine(lineId)
    }
}