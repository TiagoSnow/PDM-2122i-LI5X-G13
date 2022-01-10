package pt.isel.pdm.chess4android.multiplayer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import pt.isel.pdm.chess4android.model.GameModel
import pt.isel.pdm.chess4android.pieces.Coord
import pt.isel.pdm.chess4android.pieces.Piece
import pt.isel.pdm.chess4android.views.BoardView

class MultiplayerActivityViewModel(application: Application,
private val state: SavedStateHandle
) : AndroidViewModel(application) {

    var gameModel: GameModel = GameModel()

    fun beginBoard(boardView: BoardView) {
        gameModel.beginBoard()
        boardView.updateView(gameModel.board, gameModel.newArmyToPlay, false)
    }

    fun getAllOptions(col: Int, line: Int): MutableList<Pair<Coord, Boolean>?>? {
        return gameModel.getAllOptions(col, line)
    }

    fun movePiece(prevCoord: Coord?, newCoord: Coord?) {
        gameModel.movePiece(prevCoord, newCoord)
    }

    fun getPiece(newCoord: Coord?): Piece? {
        return gameModel.getPiece(newCoord!!.col, newCoord.line)
    }
}