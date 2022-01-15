package pt.isel.pdm.tictactoe.game

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import pt.isel.pdm.chess4android.model.Army
import pt.isel.pdm.chess4android.model.PiecesType
import pt.isel.pdm.chess4android.pieces.*
import pt.isel.pdm.tictactoe.game.model.BOARD_SIZE
import pt.isel.pdm.tictactoe.game.model.Board

/**
 * Data type used to represent the game state externally, that is, when the game state crosses
 * process boundaries and device boundaries.
 */
@Parcelize
data class GameState(
    val id: String,
    val turn: String?,
    val board: String
) : Parcelable

/**
 * Extension to create a [GameState] instance from this [Board].
 */
fun Board.toGameState(gameId: String): GameState {
    val moves: String = toArray().map { line ->
        line.map { piece: Piece? ->
            val army: String = if(piece?.army == Army.WHITE) "W" else "B"
            when (piece?.piece) {
                null -> ' '
                PiecesType.BISHOP-> "B$army"
                PiecesType.KNIGHT -> "N$army"
                PiecesType.QUEEN -> "Q$army"
                PiecesType.ROOK -> "R$army"
                PiecesType.KING -> "K$army"
                PiecesType.PAWN -> "P$army"
            }
        }.joinToString(separator = "")
    }.joinToString(separator = "")
    return GameState(id = gameId, turn = turn?.name, board = moves)
}

/**
 * Extension to create a [Board] instance from this [GameState].
 */
fun GameState.toBoard() = Board(
    turn = if (turn != null) getArmy(turn[0]) else null,
    board = board.toBoardContents()
)

/**
 * Extension to create a array of moves from this string
 */
private fun String.toBoardContents(): Array<Array<Piece?>> {
    var board: Array<Array<Piece?>> = Array(BOARD_SIZE) { Array<Piece?>(BOARD_SIZE) { null } }

    var idx = 0

    val colLimit = 7
    val lineLimit = 7
    var currCol = 0
    var currLine = 0

    while(idx < this.length) {
        when (this[idx]) {
            'B' -> board[currCol][currLine] = Bishop(getArmy(this[idx+1]), board, currCol, currLine)
            'N' -> board[currCol][currLine] = Knight(getArmy(this[idx+1]), board, currCol, currLine)
            'Q' -> board[currCol][currLine] = Queen(getArmy(this[idx+1]), board, currCol, currLine)
            'R' -> board[currCol][currLine] = Rook(getArmy(this[idx+1]), board, currCol, currLine)
            'K' -> board[currCol][currLine] = King(getArmy(this[idx+1]), board, currCol, currLine)
            'P' -> board[currCol][currLine] = Pawn(getArmy(this[idx+1]), board, currCol, currLine)
            else -> board[currCol][currLine] = null
        }
        idx += if(this[idx] == ' ') {
            1
        } else {
            2
        }


        if(currLine > lineLimit) {
            if(currCol == colLimit) {
                return board
            }
            currLine = 0
            currCol += 1
        }
        else {
            currLine += 1
        }

    }
    return board
}

fun getArmy(armyString: Char): Army = if(armyString == 'W') {
    Army.WHITE
}
else {
    Army.BLACK
}