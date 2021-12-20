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

    var showingSolution: Boolean = false
    var gameModel: GameModel = GameModel()

    fun updateBoard(pgn: String) {
        gameModel.placePieces(pgn)
    }

    fun placeSolutions(): Array<Array<Piece?>> {
        return gameModel.placeSolutionOnBoard()
    }

    fun updateSolutions(solution: ArrayList<String>) {
        gameModel.convertSolutions(solution)
    }
}