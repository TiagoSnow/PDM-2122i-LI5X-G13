package pt.isel.pdm.chess4android

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import pt.isel.pdm.chess4android.model.GameModel
import pt.isel.pdm.chess4android.pieces.Coord
import pt.isel.pdm.chess4android.pieces.Piece
import java.util.ArrayList

private const val GAME_ACTIVITY_VIEW_STATE = "GameActivity.ViewState"

class PreviewPuzzleViewModel(
    application: Application,
    private val state: SavedStateHandle
) : AndroidViewModel(application) {

    val dataOfDay: LiveData<PuzzleInfoDTO> = state.getLiveData(GAME_ACTIVITY_VIEW_STATE)

    private val _error: MutableLiveData<Throwable> = MutableLiveData()
    val error: LiveData<Throwable> = _error

    fun deletePuzzleEntity() {
        val app = getApplication<PuzzleOfDayApplication>()
        val repo = PuzzleOfDayRepository(app.puzzleOfDayService, app.historyDB.getHistoryPuzzleDao())
        repo.asyncDelete() {
        }
    }

    fun getPuzzleOfDay() {
        Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Fetching ...")
        val app = getApplication<PuzzleOfDayApplication>()
        val repo = PuzzleOfDayRepository(app.puzzleOfDayService, app.historyDB.getHistoryPuzzleDao())
        repo.fetchPuzzleOfDay { result ->
            result.onSuccess {
                state.set(GAME_ACTIVITY_VIEW_STATE, result.getOrNull())
            }
            result.onFailure {
                _error.value = it
            }
        }
        Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Returned from fetchQuoteOfDay")
    }

    var gameModel: GameModel = GameModel()
    fun updateBoard(pgn: String) {
        gameModel.placePieces(pgn)
    }

    fun getAvailableOption(col: Int, line: Int): Coord? {
        return gameModel.getAvailableOption(col, line)
    }

    fun updateSolutions(solution: ArrayList<String>) {
        gameModel.convertSolutions(solution)
    }

    fun movePiece(prevCoord: Coord?, newCoord: Coord?) {
        gameModel.movePiece(prevCoord, newCoord)
    }

    fun getPiece(newCoord: Coord?): Piece? {
        return gameModel.getPiece(newCoord!!.col, newCoord.line)
    }


}