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

class GameActivityViewModel(application: Application, private val state: SavedStateHandle) :
    AndroidViewModel(application) {

    val dataOfDay: LiveData<PuzzleInfo> = state.getLiveData(GAME_ACTIVITY_VIEW_STATE)

    fun getPuzzleOfDay() {
        this.getApplication<PuzzleOfDayApplication>()
            .puzzleOfDayService
            .getPuzzle()
            .enqueue(object : Callback<PuzzleInfo> {
                override fun onResponse(call: Call<PuzzleInfo>, response: Response<PuzzleInfo>) {
                    val puzzle = response.body()
                    if (puzzle != null && response.isSuccessful)
                        state.set(GAME_ACTIVITY_VIEW_STATE, puzzle)
                }

                override fun onFailure(call: Call<PuzzleInfo>, t: Throwable) {
                    Log.e("APP", "onFailure", t)
                }
            })
    }

    fun putAs(pgn: String) {
        val lst: List<String> = pgn.split(" ")
        for (p: String in lst) {

        }
    }

    fun setBoard(data: PuzzleInfo) {
        Log.v("APP", data.game.toString())
        Log.v("APP", data.puzzle.solution.toString())

        //return data.game.pgn;
/*
        for (key in gameKeys){

        }
*/
    }


}