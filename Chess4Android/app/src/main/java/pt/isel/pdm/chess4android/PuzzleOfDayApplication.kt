package pt.isel.pdm.chess4android

import android.app.Application
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PuzzleOfDayApplication: Application() {

    val puzzleOfDayService by lazy {
        Retrofit.Builder()
            .baseUrl("https://lichess.org/api/puzzle/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DailyPuzzleService::class.java)
    }

}