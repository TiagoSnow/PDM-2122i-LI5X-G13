package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.Army
import pt.isel.pdm.chess4android.PiecesType

abstract class Piece() {

    val MIN_BOARD_VAL = 0
    val MAX_BOARD_VAL = 7

    abstract val piece: PiecesType
    abstract val army: Army
    abstract fun movePGN(move: String)

    fun putPiece(col: Int, line: Int, piece: Piece) {
        board[col][line] = piece
    }

    fun removePiece(col: Int, line: Int) {
        board[col][line] = null
    }

}