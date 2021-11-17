package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.Army
import pt.isel.pdm.chess4android.Pieces

abstract class Piece {

    abstract var army: Army
    abstract var piece: Pieces

    val MIN_BOARD_VAL = 0
    val MAX_BOARD_VAL = 7

    abstract fun movePGN(move: String)

    private fun checkIfPieceExists(col: Int, line: Int, army: Army, pieces: Pieces): Boolean {
        return (board[col][line] != null
                && board[col][line]?.first == army
                && board[col][line]?.second == pieces)
    }

    fun putPiece(col: Int, line: Int, piece: Piece) {
        board[col][line] = piece
    }

    fun removePiece(col: Int, line: Int) {
        board[col][line] = null
    }

}