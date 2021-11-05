package pt.isel.pdm.chess4android

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.google.gson.internal.LinkedTreeMap
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

private const val GAME_ACTIVITY_VIEW_STATE = "GameActivity.ViewState"

class GameActivityViewModel(application: Application, private val state: SavedStateHandle): AndroidViewModel(application) {

    val dataOfDay: LiveData<Data> = state.getLiveData(GAME_ACTIVITY_VIEW_STATE)

    fun getPuzzleOfDay() {
        this.getApplication<PuzzleOfDayApplication>()
            .puzzleOfDayService
            .getPuzzle()
            .enqueue(object: Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                val puzzle = response.body()
                if(puzzle != null && response.isSuccessful)
                    state.set(GAME_ACTIVITY_VIEW_STATE, puzzle)
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                Log.e("APP", "onFailure", t)
            }
        })
    }

    fun addData(data: Data) {
        val gameData = data.game
        val gameKeys = gameData.keys
        for (key in gameKeys){
            Log.v("APP", key)
        }
    }

    /*fun getPuzzleOfDay(completion: (Puzzle) -> Unit) {
        //Let's make the request
        service.getPuzzle().enqueue(object: Callback<Puzzle> {
            override fun onResponse(call: Call<Puzzle>, response: Response<Puzzle>) {
                response.body()?.let { completion(it) }
            }

            override fun onFailure(call: Call<Puzzle>, t: Throwable) {
                Log.e("APP", "onFailure", t)
            }

        })
    }*/

}