package pt.isel.pdm.chess4android

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.ViewModel
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class GameActivityViewModel: ViewModel() {
    companion object {
        val service = Retrofit.Builder()
            .baseUrl("https://lichess.org/api/puzzle/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChessRoyaleService::class.java)
    }

    fun getPuzzleOfDay(completion: (Test) -> Unit) {
        //Let's make the request
        service.getPuzzle().enqueue(object: Callback<Test> {
            override fun onResponse(call: Call<Test>, response: Response<Test>) {
                response.body()?.let { completion(it) }
            }

            override fun onFailure(call: Call<Test>, t: Throwable) {
                Log.e("APP", "onFailure", t)
            }

        })
    }

}