package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.Army
import pt.isel.pdm.chess4android.PiecesType

abstract class Piece {

    val MIN_BOARD_VAL = 0
    val MAX_BOARD_VAL = 7
    abstract val board: Array<Array<Piece?>>

    abstract var col: Int
    abstract var line: Int

    abstract var piece: PiecesType
    abstract val army: Army
    abstract fun movePGN(move: String)

    fun checkIfPieceExists(col: Int, line: Int, army: Army, piece: PiecesType): Boolean {
        return (board[col][line] != null
                && board[col][line]?.army == army
                && board[col][line]?.piece == piece)
    }

    fun putPiece(col: Int, line: Int, piece: Piece) {
        board[col][line] = piece
        board[col][line]?.col = col
        board[col][line]?.line = line

    }

    fun removePiece(col: Int, line: Int) {
        board[col][line] = null
    }

    abstract fun searchRoute() : MutableList<Pair<Coord, Boolean>?>

}