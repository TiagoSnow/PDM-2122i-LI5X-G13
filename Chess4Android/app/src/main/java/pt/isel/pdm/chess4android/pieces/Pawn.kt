package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.Army
import pt.isel.pdm.chess4android.PiecesType

class Pawn(override val army: Army, override var board: Array<Array<Piece?>>, override var col: Int, override var line: Int) : Piece() {

    override val piece = PiecesType.PAWN

    override fun movePGN(move: String) {
        val col: Int
        val line: Int
        var startingPoint = 0
        if (move.length == 4) {
            col = move[2] - 'a'
            line = 8 - move[3].digitToInt()
            val startColumn = move[0] - 'a'
            for (l in 0..7) {
                if (checkIfPieceExists(startColumn, l, army, piece)) {
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
                checkIfPieceExists(col, 1, army, piece) -> startingPoint = 1
                checkIfPieceExists(col, 2, army, piece) -> startingPoint = 2
                checkIfPieceExists(col, 5, army, piece) -> startingPoint = 5
                checkIfPieceExists(col, 6, army, piece) -> startingPoint = 6
            } else
                startingPoint = if (army == Army.WHITE) line + 1 else line - 1
        }

        removePiece(col, startingPoint)
        putPiece(col, line, this)
    }

    override fun searchRoute(): MutableList<Pair<Coord, Boolean>?> {
        return getAllAvailableOptions()
    }

    private fun getAllAvailableOptions(): MutableList<Pair<Coord, Boolean>?> {
        var list = mutableListOf<Pair<Coord, Boolean>?>()

        // front and back
        if (board[col][line]?.army == Army.WHITE && line == 6 && line - 1 in 0..7) {
            for (lineAux in line - 1 downTo line - 2) {
                if (board[col][lineAux]?.army == army) {
                    break
                }
                list.add(Pair(Coord(col, lineAux), false))
            }
        } else if (board[col][line - 1] == null && board[col][line]?.army == Army.WHITE && line - 1 in 0..7) {
            list.add(Pair(Coord(col, line - 1), false))
        } else if (line == 1 && board[col][line]?.army == Army.BLACK && line + 1 in 0..7) {
            for (lineAux in line + 1 until line + 3) {
                if (board[col][lineAux]?.army == army) {
                    break
                }
                list.add(Pair(Coord(col, lineAux), false))
            }
        } else if (board[col][line + 1] == null && board[col][line]?.army == Army.BLACK && line + 1 in 0..7)
            list.add(Pair(Coord(col, line + 1), false))

        //to eat diagonal
        if (board[col][line]?.army == Army.WHITE && col + 1 in 0..7 && col - 1 in 0..7 && line - 1 in 0..7) {
            if (board[col + 1][line - 1]?.army != army && board[col + 1][line - 1] != null) list.add(
                Pair(Coord(col + 1, line - 1), true)
            )
            if (board[col - 1][line - 1]?.army != army && board[col - 1][line - 1] != null) list.add(
                Pair(Coord(col - 1, line - 1), true)
            )
        } else if (col + 1 in 0..7 && col - 1 in 0..7 && line + 1 in 0..7) {
            if (board[col + 1][line + 1]?.army != army && board[col + 1][line + 1] != null) list.add(
                Pair(Coord(col + 1, line + 1), true)
            )
            if (board[col - 1][line + 1]?.army != army && board[col - 1][line + 1] != null) list.add(
                Pair(Coord(col - 1, line + 1), true)
            )
        }

        return list
    }

}