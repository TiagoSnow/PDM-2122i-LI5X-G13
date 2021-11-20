package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.Army
import pt.isel.pdm.chess4android.PiecesType

class Pawn(
    override val army: Army,
    override var board: Array<Array<Piece?>>,
    override var col: Int,
    override var line: Int
) : Piece() {

    override var piece = PiecesType.PAWN

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
        val list = mutableListOf<Pair<Coord, Boolean>?>()
        val currPiece = board[col][line]
        val isWhite = currPiece?.army == Army.WHITE

        list.addAll(verticalOptions(isWhite))

        //to eat diagonal
        if (isWhite) {
            if (line - 1 in 0..7) {
                if (col + 1 in 0..7) {
                    if (board[col + 1][line - 1]?.army != army && board[col + 1][line - 1] != null)
                        list.add(Pair(Coord(col + 1, line - 1), true))
                }
                if (col - 1 in 0..7) {
                    if (board[col - 1][line - 1]?.army != army && board[col - 1][line - 1] != null)
                        list.add(Pair(Coord(col - 1, line - 1), true))
                }
            }
        } else if (line + 1 in 0..7) {
            if (col + 1 in 0..7) {
                if (board[col + 1][line + 1]?.army != army && board[col + 1][line + 1] != null)
                    list.add(Pair(Coord(col + 1, line + 1), true))
            }
            if (col - 1 in 0..7) {
                if (board[col - 1][line + 1]?.army != army && board[col - 1][line + 1] != null)
                    list.add(Pair(Coord(col - 1, line + 1), true))
            }
        }
        return list
    }

    private fun verticalOptions(isWhite: Boolean): MutableList<Pair<Coord, Boolean>?> {
        val list = mutableListOf<Pair<Coord, Boolean>?>()
        //if movement is up or down
        val moveAux = if (isWhite) -1 else 1
        // line in which pawn can move 2 spaces
        val moveTwo = if (isWhite) 6 else 1

        if (line + moveAux in 0..7 && board[col][line + moveAux] is Piece) {
            return mutableListOf()
        }
        if (line + moveAux in 0..7 && board[col][line + moveAux] == null)
            list.add(Pair(Coord(col, line + moveAux), false))

        if (line == moveTwo && board[col][line + moveAux * 2] == null)
            list.add(Pair(Coord(col, line + moveAux * 2), false))

        return list
    }

    fun searchRouteToEat(): MutableList<Pair<Coord, Boolean>?> {
        val list = mutableListOf<Pair<Coord, Boolean>?>()
        var pair: Pair<Coord, Boolean>?
        if (army == Army.WHITE) {
            pair = searchDirection(col - 1, line - 1)        //up left
            if (pair != null) {
                list.add((pair))
            }
            pair = searchDirection(col + 1, line - 1)       //up right
            if (pair != null) {
                list.add((pair))
            }
        } else {
            pair = searchDirection(col - 1, line + 1)        //down left
            if (pair != null) {
                list.add((pair))
            }
            pair = searchDirection(col + 1, line + 1)       //down right
            if (pair != null) {
                list.add((pair))
            }
        }
        return list
    }

    private fun searchDirection(col: Int, line: Int): Pair<Coord, Boolean>? {
        if (col !in 0..7 || line !in 0..7) {
            return null
        }
        if (board[col][line] == null) {
            return Pair(Coord(col, line), false)
        }
        return null
    }

}