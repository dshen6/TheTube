package bus.the.ride.thetube.util

/**
 * Created by Shen on 2/5/2018.
 */
object NullableUtils {

    fun <T1 : Any, T2 : Any, R : Any> ifNotNull(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
        return if (p1 != null && p2 != null) block(p1, p2) else null
    }
}