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
    var initialBoard: Array<Array<Piece?>> = Array(8) { Array<Piece?>(8) { null } }
    var solutionBoard: Array<Array<Piece?>> = Array(8) { Array<Piece?>(8) { null } }

    fun updateBoard(pgn: String) {
        gameModel.placePieces(pgn)
        initialBoard = gameModel.board.clone()
    }

    fun placeSolutions() {
        gameModel.placeSolutionOnBoard()
        solutionBoard = gameModel.board.clone()
    }

    fun updateSolutions(solution: ArrayList<String>) {
        gameModel.convertSolutions(solution)
    }
}