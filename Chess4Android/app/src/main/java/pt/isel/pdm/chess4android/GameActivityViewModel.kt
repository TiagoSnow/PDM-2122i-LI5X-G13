package pt.isel.pdm.chess4android

import android.util.Log
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GameActivityViewModel: ViewModel() {
    companion object {
        val service = Retrofit.Builder()
            .baseUrl("https://lichess.org/api/puzzle/daily/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChessRoyaleService::class.java)

    }

    fun getPuzzleOfDay(completion: (String) -> Unit) {
        //Let's make the request
        service.getPuzzle().enqueue(object: Callback<Test> {
            override fun onResponse(call: Call<Test>, response: Response<Test>) {
                completion(response.body()?.game ?: "")
            }

            override fun onFailure(call: Call<Test>, t: Throwable) {
                Log.e("APP_TAG", "onFailure", t)
            }

        })
    }

}