package pt.isel.pdm.chess4android

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import retrofit2.Call
import retrofit2.http.GET
import java.util.*

/**
 * Represents data returned by the remote API. It's [Parcelable] so that it can also be used
 * locally (in the device) to exchange data between activities and as a means to preserve state.
 */
@Parcelize
data class PuzzleInfoDTO(val game: Game, val puzzle: Puzzle, val date: String, val status: String): Parcelable

/**
 * Represents part of the data returned by the remote API. It's existence is due to the API design.
 * As simple as that.
 */
@Parcelize
data class PuzzleInfo(val game: Game, val puzzle: Puzzle) : Parcelable
@Parcelize
data class Game(val pgn: String) : Parcelable
@Parcelize
data class Puzzle(val id: String, var solution: ArrayList<String>) : Parcelable


fun convertArrayToString(list: ArrayList<String>): String {
    var l = list.toString()
    var array = l.substring(1, l.length - 1)
    return array
}


//"f3c3,c8b8,d1g4"
fun convertStringToArray(solution: String): ArrayList<String> {
    var s = solution.split(", ")
    var array =  s as ArrayList<String>
    return array
}


/**
 * The abstraction that represents accesses to the remote API's resources.
 */
interface DailyPuzzleService {
    @GET("daily")
    fun getPuzzle(): Call<PuzzleInfo>
}

/**
 * Represents errors while accessing the remote API. Instead of tossing around Retrofit errors,
 * we can use this exception to wrap them up.
 */
class ServiceUnavailable(message: String = "", cause: Throwable? = null) : Exception(message, cause)
