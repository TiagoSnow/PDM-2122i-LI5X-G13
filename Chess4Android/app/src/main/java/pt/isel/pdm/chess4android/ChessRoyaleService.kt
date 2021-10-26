package pt.isel.pdm.chess4android

import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.GET
import java.util.*

data class Test(val game: Object, val puzzle: Object)

interface ChessRoyaleService {

    @GET("daily")
    fun getPuzzle(): Call<Test>

}