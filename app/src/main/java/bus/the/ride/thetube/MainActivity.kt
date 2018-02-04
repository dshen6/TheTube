package bus.the.ride.thetube

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelStores
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import io.reactivex.functions.Consumer

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        val store = ViewModelStores.of(this)
        val provider = ViewModelProvider(store, factory)
        val model = provider.get(ArrivalsListViewModel::class.java)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ArrivalsListAdapter(ArrayList())

        model.observe(Consumer { viewState ->
            Log.d("view state ", viewState.toString()  + " "+ model)
            when (viewState) {
                is ViewState.Empty -> {

                }
                is ViewState.Loading -> {

                }
                is ViewState.Error -> {

                }
                is ViewState.DataReady -> {
                    (recyclerView.adapter as ArrivalsListAdapter).setData(viewState.data)
                }
            }
        })
    }
}
