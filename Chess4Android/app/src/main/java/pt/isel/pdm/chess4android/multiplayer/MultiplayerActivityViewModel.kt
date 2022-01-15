package pt.isel.pdm.chess4android.multiplayer

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pt.isel.pdm.chess4android.PuzzleOfDayApplication
import pt.isel.pdm.chess4android.model.Army
import pt.isel.pdm.chess4android.model.MultiplayerModel
import pt.isel.pdm.chess4android.model.PiecesType
import pt.isel.pdm.chess4android.pieces.Coord
import pt.isel.pdm.chess4android.pieces.Piece
import pt.isel.pdm.tictactoe.game.GameState
import pt.isel.pdm.tictactoe.game.toBoard
import pt.isel.pdm.chess4android.model.Board
import pt.isel.pdm.tictactoe.game.toGameState

class MultiplayerActivityViewModel(
    application: Application,
    var initialGameState: GameState?,
    var localPlayer: Army?,
) : AndroidViewModel(application) {

    private var isBeginOfGame: Boolean = true

    var gameModel: MultiplayerModel = MultiplayerModel()


    init {
        beginBoard()
    }

    private val _game: MutableLiveData<Result<Board>> by lazy {
        MutableLiveData(Result.success(getOnlineBoard()/*initialGameState!!.toBoard()*/))
    }

    private fun getOnlineBoard(): Board {
        return Board(getTurn(), getBoard())
    }

    val game: LiveData<Result<Board>> = _game

    private val gameSubscription = getApplication<PuzzleOfDayApplication>()
        .gamesRepository.subscribeToGameStateChanges(
            challengeId = initialGameState!!.id,
            onSubscriptionError = { _game.value = Result.failure(it) },
            onGameStateChange = { _game.value = Result.success(it.toBoard()) }
        )

    /**
     * View model is destroyed
     */
    override fun onCleared() {
        super.onCleared()
        getApplication<PuzzleOfDayApplication>().gamesRepository.deleteGame(
            challengeId = initialGameState!!.id,
            onComplete = { }
        )
        gameSubscription.remove()
    }

    fun beginBoard() {
        gameModel.beginBoard()
        /*_game.value!!.onSuccess {
            it.beginBoard()
        }*/
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
                gameState = newBoard.toGameState(initialGameState!!.id, getNextTurn()),
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
        Log.v("TEST","my localPlayer is "+localPlayer)
        if(localPlayer != null && getNextArmyToPlay() != localPlayer) {
            gameModel.setLocalPlayerArmy(localPlayer!!)
            gameModel.newArmyToPlay = localPlayer
        }
    }

    fun isThisPlayerTurn(): Boolean {
        if(localPlayer == null) return true
        val turn = getTurn()
        return localPlayer == turn && turn == getNextArmyToPlay()
    }

    fun getTurn(): Army {
        if(initialGameState == null) return getNextArmyToPlay()
        return gameModel.getArmy(initialGameState?.turn == "WHITE")
    }

    fun getNextTurn(): String {
        val turn = getTurn()
        return if(turn == Army.BLACK) {
            Army.WHITE.name
        } else {
            Army.BLACK.name
        }
    }

    fun updateBoardFromOnline(board: Array<Array<Piece?>>, turn: Army?) {
        if(isBeginOfGame){
            isBeginOfGame = false
            return
        }
        if(_game.value == null) return
        gameModel.updateBoardFromOnline(board)
        initialGameState!!.turn = turn!!.name
    }

    fun updateBoard(prevCoord: Coord, newCoord: Coord) {
        _game.value?.onSuccess {
            it.makeMove(newCoord, prevCoord)
        }
    }
}

