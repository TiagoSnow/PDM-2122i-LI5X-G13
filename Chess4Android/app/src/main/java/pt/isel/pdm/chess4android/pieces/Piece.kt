package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.Army
import pt.isel.pdm.chess4android.Piece

abstract class Piece {
    abstract fun movePGN(move: String, army: Army)
    private fun checkIfPieceExists(col: Int, line: Int, army: Army, piece: Piece): Boolean {
        return (board[col][line] != null
                && board[col][line]?.first == army
                && board[col][line]?.second == piece)
    }
}