package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.Army
import pt.isel.pdm.chess4android.PiecesType

class King(override var army: Army) : Piece() {

    override var piece = PiecesType.KING

    override fun movePGN(move: String) {
        val col: Int
        val line: Int

        if (move.length == 4) {
            col = move[2] - 'a'
            line = 8 - move[3].digitToInt()
        } else {
            col = move[1] - 'a'
            line = 8 - move[2].digitToInt()
        }
        val startPositions = searchKing(col, line, army)

        removePiece(startPositions.first, startPositions.second)
        putPiece(col, line, this)
    }

    private fun searchKing(col: Int, line: Int, army: Army): Pair<Int, Int> {
        val piece = pt.isel.pdm.chess4android.PiecesType.KING

        if (col - 1 in 0..7 && line - 1 in 0..7)
            if (checkIfPieceExists(col - 1, line - 1, army, piece)) return Pair(
                col - 1,
                line - 1
            )      //diagonal up left

        if ((col in 0..7) && line - 1 in 0..7)
            if (checkIfPieceExists(col, line - 1, army, piece)) return Pair(
                col,
                line - 1
            )              //up

        if (col + 1 in 0..7 && line - 1 in 0..7)
            if (checkIfPieceExists(col + 1, line - 1, army, piece)) return Pair(
                col + 1,
                line - 1
            )      //diagonal up right

        if (col - 1 in 0..7 && line in 0..7)
            if (checkIfPieceExists(col - 1, line, army, piece)) return Pair(
                col - 1,
                line
            )              //left

        if (col + 1 in 0..7 && line in 0..7)
            if (checkIfPieceExists(col + 1, line, army, piece)) return Pair(
                col + 1,
                line
            )              //right

        if (col - 1 in 0..7 && line + 1 in 0..7)
            if (checkIfPieceExists(col - 1, line + 1, army, piece)) return Pair(
                col - 1,
                line + 1
            )      //diagonal down left

        if ((col in 0..7) && line + 1 in 0..7)
            if (checkIfPieceExists(col, line + 1, army, piece)) return Pair(
                col,
                line + 1
            )             //down

        if (col + 1 in 0..7 && line + 1 in 0..7)
            if (checkIfPieceExists(col + 1, line + 1, army, piece)) return Pair(
                col + 1,
                line + 1
            )     //diagonal down right

        return Pair(-1, -1);
    }

}