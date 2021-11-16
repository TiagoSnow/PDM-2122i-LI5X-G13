package pt.isel.pdm.chess4android

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import retrofit2.Call
import retrofit2.http.GET
import java.util.*

@Parcelize
data class PuzzleInfo(val game: Game, val puzzle: Puzzle) : Parcelable
@Parcelize
data class Game(val pgn: String) : Parcelable
@Parcelize
data class Puzzle(val id: String, var solution: ArrayList<String>) : Parcelable

interface DailyPuzzleService {
    @GET("daily")
    fun getPuzzle(): Call<PuzzleInfo>
}
