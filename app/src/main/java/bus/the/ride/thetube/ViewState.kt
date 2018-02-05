package bus.the.ride.thetube

/**
 * Created by Shen on 1/31/2018.
 */
sealed class ViewState<T> {

    class Init<T>: ViewState<T>()
    class Error<T>: ViewState<T>()
    class Empty<T>: ViewState<T>()
    class Loading<T>: ViewState<T>()
    class DataReady<T>(val data : T): ViewState<T>()
}