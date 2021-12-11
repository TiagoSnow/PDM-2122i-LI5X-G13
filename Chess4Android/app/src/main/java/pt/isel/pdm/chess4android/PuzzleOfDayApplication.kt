package pt.isel.pdm.chess4android

import android.app.Application
import androidx.room.Room
import pt.isel.pdm.chess4android.history.HistoryDataAccess
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import pt.isel.pdm.chess4android.history.HistoryDataAccess.*

const val APP_TAG = "PuzzleOfDay"

class PuzzleOfDayApplication: Application() {

    val puzzleOfDayService by lazy {
        Retrofit.Builder()
            .baseUrl("https://lichess.org/api/puzzle/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DailyPuzzleService::class.java)
    }

    /**
     * The database that contains the "puzzles of day" fetched so far.
     */
    val historyDB: HistoryDatabase by lazy {
        Room
            .databaseBuilder(this, HistoryDatabase::class.java, "History_db")
            .build()
    }

}