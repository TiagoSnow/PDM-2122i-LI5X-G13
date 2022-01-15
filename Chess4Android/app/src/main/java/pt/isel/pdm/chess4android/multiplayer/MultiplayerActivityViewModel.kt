package pt.isel.pdm.chess4android.multiplayer

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import pt.isel.pdm.chess4android.PuzzleOfDayApplication
import pt.isel.pdm.chess4android.model.Army
import pt.isel.pdm.chess4android.model.MultiplayerModel
import pt.isel.pdm.chess4android.model.PiecesType
import pt.isel.pdm.chess4android.pieces.Coord
import pt.isel.pdm.chess4android.pieces.Piece
import pt.isel.pdm.tictactoe.game.GameState
import pt.isel.pdm.chess4android.model.Board
import pt.isel.pdm.tictactoe.game.toGameState

class MultiplayerActivityViewModel(
    application: Application,
    private val state: SavedStateHandle
) : AndroidViewModel(application) {

    var gameModel: MultiplayerModel = MultiplayerModel()
    var initialGameState: GameState? = null
    var localPlayer: Army? = null

    fun beginBoard() {
        gameModel.beginBoard()
    }

    fun getAllOptions(col: Int, line: Int): MutableList<Pair<Coord, Boolean>?>? {
        return gameModel.getMoveOptions(col, line)
    }

    fun movePiece(prevCoord: Coord?, newCoord: Coord?) {
        Log.v("TEST",""+ localPlayer + "-->" + gameModel.newArmyToPlay)
            gameModel.movePiece(prevCoord, newCoord)

        if(localPlayer != null) {
            val newBoard = Board(getNextArmyToPlay(), gameModel.board)
            getApplication<PuzzleOfDayApplication>().gamesRepository.updateGameState(
                gameState = newBoard.toGameState(initialGameState!!.id),
                onComplete = { result ->
                    if(result.isFailure)
                        throw IllegalStateException("Error updating board at player: $localPlayer")
                }
            )
        }
    }

    fun getPiece(newCoord: Coord?): Piece {
        return gameModel.getPiece(newCoord!!.col, newCoord.line)!!
    }

    fun getNextArmyToPlay(): Army {
        return gameModel.newArmyToPlay
    }

    fun currPieceArmy(col: Int, line: Int): Army {
        return getPiece(Coord(col, line))!!.army
    }

    fun switchArmy() {
        gameModel.switchArmy()
    }

    fun isChecking(option: Pair<Coord, Boolean>?): Boolean {
        return gameModel.isChecking(option)
    }

    fun verifyPiecePromotion(newCoord: Coord?): Boolean {
        return gameModel.verifyPiecePromotion(newCoord)
    }

    fun promotePiece(newCoord: Coord?, pieceType: PiecesType) {
        return gameModel.promotePiece(newCoord,pieceType)
    }

    fun getBoard(): Array<Array<Piece?>> {
        return gameModel.board
    }

    fun doubleCheck() {
        gameModel.doubleCheck()
    }

    fun isCheckMate(): Boolean {
        return gameModel.isCheckMate()
    }

    fun updateOnlineCurrArmy(initialState: GameState?, localPlayer: Army?) {
        initialGameState = initialState
        this.localPlayer = localPlayer
        Log.v("TEST","my localPlayer is "+localPlayer)
        if(this.localPlayer != null && getNextArmyToPlay() != this.localPlayer) {
            gameModel.setLocalPlayerArmy(localPlayer!!)
            gameModel.newArmyToPlay = localPlayer
        }
    }
}