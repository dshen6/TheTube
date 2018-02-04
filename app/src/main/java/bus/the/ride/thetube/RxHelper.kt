package bus.the.ride.thetube

import android.util.Log
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import java.util.concurrent.TimeUnit

/**
 * Created by Shen on 2/3/2018.
 */
operator fun CompositeDisposable.plusAssign(disposable: Disposable)  {
    if (!add(disposable)) {
        Log.wtf("CompositeDisposableHelper","adding to disposed compositeDisposable failed")
    }
}

fun <T> Single<T>.exponentialBackoff(maxRetryCount: Int): Single<T> {
    return retryWhen({ error ->
        error.zipWith(Flowable.range(1, maxRetryCount), BiFunction<Throwable, Int, Int> { err, count -> count })
                .flatMap { retryCount ->
                    Log.v("RETRY","Retrying... retryCount = $retryCount")
                    // exponential backoff before next retry (after 2 ^ retryCount seconds)
                    Flowable.timer(Math.pow(2.toDouble(), retryCount.toDouble()).toLong(), TimeUnit.SECONDS)
                }
    })
}