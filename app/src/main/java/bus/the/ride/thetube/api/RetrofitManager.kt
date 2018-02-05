package bus.the.ride.thetube.api

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Shen on 2/4/2018.
 */
class RetrofitManager {

    companion object {
        private const val APP_ID = "5ae3f9a3"
        private const val APP_KEY = "e288b404bfb0b24d98dab6455df30eb6"

        val instance: Retrofit by lazy {
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
    }
}