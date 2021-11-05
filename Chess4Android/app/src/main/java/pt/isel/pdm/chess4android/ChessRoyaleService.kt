package pt.isel.pdm.chess4android

import android.os.Parcelable
import com.google.gson.internal.LinkedTreeMap
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import retrofit2.Call
import retrofit2.http.GET

@Parcelize
data class Data(val game: LinkedTreeMap<String, Any>, val puzzle: @RawValue Object) : Parcelable

interface ChessRoyaleService {

    @GET("daily")
    fun getPuzzle(): Call<Data>

}