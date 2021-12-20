package pt.isel.pdm.chess4android

import android.app.Application
import androidx.room.Room
import androidx.work.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import pt.isel.pdm.chess4android.history.HistoryDataAccess.*
import java.util.concurrent.TimeUnit

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


    /**
     * Called each time the application process is loaded
     */
    override fun onCreate() {
        super.onCreate()
        val workRequest = PeriodicWorkRequestBuilder<DownloadDailyPuzzleWorker>(1, TimeUnit.DAYS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .setRequiresStorageNotLow(true)
                    .build()
            )
            .build()

        WorkManager
            .getInstance(this)
            .enqueueUniquePeriodicWork(
                "DownloadDailyPuzzle",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }
}