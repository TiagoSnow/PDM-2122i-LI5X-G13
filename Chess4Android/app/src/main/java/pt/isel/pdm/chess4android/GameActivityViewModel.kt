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

class GameActivityViewModel(
    application: Application,
    private val state: SavedStateHandle
) : AndroidViewModel(application) {

    private lateinit var currentPuzzleInfoDTO: PuzzleInfoDTO

    val dataOfDay: LiveData<PuzzleInfoDTO> = state.getLiveData(GAME_ACTIVITY_VIEW_STATE)

    /**
     * The [LiveData] instance used to publish errors that occur while fetching the quote of day
     */
    private val _error: MutableLiveData<Throwable> = MutableLiveData()
    val error: LiveData<Throwable> = _error

    fun deletePuzzleEntity() {
        val app = getApplication<PuzzleOfDayApplication>()
        val repo = PuzzleOfDayRepository(app.puzzleOfDayService, app.historyDB.getHistoryPuzzleDao())
        repo.asyncDelete() {
        }
    }

//TO TEST
    fun getAllPuzzleEntity() {
        val app = getApplication<PuzzleOfDayApplication>()
        val repo = PuzzleOfDayRepository(app.puzzleOfDayService, app.historyDB.getHistoryPuzzleDao())
        repo.asyncGetAll() {
        }
    }

    fun updatePuzzleEntity() {
        val app = getApplication<PuzzleOfDayApplication>()
        val repo = PuzzleOfDayRepository(app.puzzleOfDayService, app.historyDB.getHistoryPuzzleDao())
        repo.asyncUpdate(currentPuzzleInfoDTO){
        }
    }

    fun getPuzzleOfDay() {
        Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Fetching ...")
        val app = getApplication<PuzzleOfDayApplication>()
        val repo = PuzzleOfDayRepository(app.puzzleOfDayService, app.historyDB.getHistoryPuzzleDao())
        repo.fetchPuzzleOfDay { result ->
            result.onSuccess {
                Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Returned success from fetchPuzzleOfDay")
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

    fun getAvailableSolution(col: Int, line: Int): Coord? {
        return gameModel.getAvailableSolution(col, line)
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

    fun getSolutionsSize(solution: ArrayList<String>): Int {
        return solution.size
    }

    fun setCurrentPuzzleInfoDTO(puzzle: PuzzleInfoDTO) {
        this.currentPuzzleInfoDTO = puzzle
    }

    fun getCurrentPuzzleInfoDTO(): PuzzleInfoDTO {
        return currentPuzzleInfoDTO
    }

    fun removeSolutionSelected(pair: Pair<Coord?, Coord?>): Boolean {
        return gameModel.removeSolutionSelected(pair)
    }
}