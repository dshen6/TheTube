package bus.the.ride.thetube

import android.util.Log
import bus.the.ride.thetube.models.ArrivalPrediction
import bus.the.ride.thetube.models.StationInRadius
import bus.the.ride.thetube.models.StationsInRadiusResponse
import bus.the.ride.thetube.models.Stop
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
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

        private const val APP_ID = "5ae3f9a3"
        private const val APP_KEY = "e288b404bfb0b24d98dab6455df30eb6"
        private const val TUBE_STATION_STOP_TYPE = "NaptanMetroStation"

        private const val DEFAULT_RADIUS = 1000
        private const val DEFAULT_LATITUDE = 51.4999359F // Palace of Westminster
        private const val DEFAULT_LONGITUDE = -0.1274875F

        private const val RETRY_PERIOD_SEC = 30L

        private val retrofitInstance: Retrofit by lazy {
            Retrofit.Builder()
                    .client(okHttpInstance)
                    .baseUrl("https://api.tfl.gov.uk")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                    .addConverterFactory(GsonConverterFactory.create(gsonInstance))
                    .build()
        }

        private val gsonInstance: Gson = GsonBuilder().create()

        private val okHttpInstance: OkHttpClient = OkHttpClient.Builder().addNetworkInterceptor({
            val originalHttpUrl = it.request().url()

            val url = originalHttpUrl.newBuilder()
                    .addQueryParameter("app_id", APP_ID)
                    .addQueryParameter("app_key", APP_KEY)
                    .build()

            // Request customization: add request headers
            val requestBuilder = it.request().newBuilder().url(url)

            Log.d("TUBEAPI", "url is " + url.toString())

            val request = requestBuilder.build()
            it.proceed(request)
        }).build()

        val instance: TubeApi by lazy { TubeApi(retrofitInstance.create(TubeService::class.java)) }
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
        fun getStopsForLine(@Path("lineId") lineId: String) : Single<List<Stop>>
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