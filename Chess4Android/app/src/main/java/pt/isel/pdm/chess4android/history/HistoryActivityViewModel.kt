package pt.isel.pdm.chess4android.history

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pt.isel.pdm.chess4android.*
import pt.isel.pdm.chess4android.history.HistoryDataAccess.*

const val APP_TAG = "PuzzleOfDay"

class HistoryActivityViewModel(application: Application): AndroidViewModel(application)  {

    /**
     * Holds a [LiveData] with the list of quotes, or null if it has not yet been requested by
     * the [HistoryActivity] through a call to [loadHistory]
     */
    var history: LiveData<List<PuzzleInfoDTO>>? = null
        private set

    private val historyDao : HistoryPuzzleDao by lazy {
        getApplication<PuzzleOfDayApplication>().historyDB.getHistoryPuzzleDao()
    }


    /**
     * Gets the puzzles list (history) from the DB.
     */
    fun loadHistory(): LiveData<List<PuzzleInfoDTO>> {
        val publish = MutableLiveData<List<PuzzleInfoDTO>>()
        history = publish
        callbackAfterAsync(
            asyncAction = {
                Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Getting history from local DB")
                historyDao.getAll().map {
                    PuzzleInfoDTO(
                        game = Game(it.pgn),
                        puzzle = Puzzle(it.name, convertStringToArray(it.solution)),
                        date = it.id,
                        status = it.status)
                }
            },
            callback = { result ->
                Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: mapping results")
                result.onSuccess { publish.value = it }
                result.onFailure { publish.value = emptyList() }
            }
        )
        return publish
    }



}