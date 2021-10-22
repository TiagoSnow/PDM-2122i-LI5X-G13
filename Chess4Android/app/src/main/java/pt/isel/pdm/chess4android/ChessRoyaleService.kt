package pt.isel.pdm.chess4android

import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.GET

data class Test(val game: String)

interface ChessRoyaleService {


    @GET("/")
    fun getPuzzle(): Call<Test>

}