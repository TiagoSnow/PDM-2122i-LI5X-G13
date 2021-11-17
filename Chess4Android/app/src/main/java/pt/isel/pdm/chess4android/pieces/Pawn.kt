package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.Army
import pt.isel.pdm.chess4android.Pieces

class Pawn(override var army: Army) : Piece() {

    override var piece = Pieces.PAWN

    override fun movePGN(move: String) {
        val col: Int
        val line: Int
        var startingPoint = 0
        if (move.length == 4) {
            col = move[2] - 'a'
            line = 8 - move[3].digitToInt()
            val startColumn = move[0] - 'a'
            for (l in 0..7) {
                if (checkIfPieceExists(startColumn, l, army, pt.isel.pdm.chess4android.Pieces.PAWN)) {
                    startingPoint = l
                    break
                }
            }
        } else {
            col = move[0] - 'a'
            line = 8 - move[1].digitToInt()
            if ((line == 3) && army == Army.BLACK ||
                (line == 4) && army == Army.WHITE
            ) when {
                //mais clean?
                checkIfPieceExists(col, 1, army, pt.isel.pdm.chess4android.Pieces.PAWN) -> startingPoint = 1
                checkIfPieceExists(col, 2, army, pt.isel.pdm.chess4android.Pieces.PAWN) -> startingPoint = 2
                checkIfPieceExists(col, 5, army, pt.isel.pdm.chess4android.Pieces.PAWN) -> startingPoint = 5
                checkIfPieceExists(col, 6, army, pt.isel.pdm.chess4android.Pieces.PAWN) -> startingPoint = 6
            } else
                startingPoint = if (army == Army.WHITE) line + 1 else line - 1
        }

        removePiece(col, startingPoint)
        putPiece(col, line, this)
    }
}