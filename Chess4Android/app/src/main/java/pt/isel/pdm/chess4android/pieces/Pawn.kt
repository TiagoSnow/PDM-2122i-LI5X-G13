package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.model.Army
import pt.isel.pdm.chess4android.model.PiecesType

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
        var toConvert: Char

        //TODO UPGRADE PAWN
        if ('=' in move) {

            return
        }


        else if (move.length == 4) {
            col = move[2] - 'a'
            line = 8 - move[3].digitToInt()
            val startColumn = move[0] - 'a'
            for (l in 0..7) {
                if (checkIfPieceExists(startColumn, l, army, piece)) {
                    startingPoint = l
                    break
                }
            }
            removePiece(startColumn, startingPoint)
        } else {
            col = move[0] - 'a'
            line = 8 - move[1].digitToInt()
            if ((line == 3) && army == Army.BLACK ||
                (line == 4) && army == Army.WHITE
            )
                startingPoint = when {
                    checkIfPieceExists(col, 1, army, piece) -> 1
                    checkIfPieceExists(col, 2, army, piece) -> 2
                    checkIfPieceExists(col, 5, army, piece) -> 5
                    checkIfPieceExists(col, 6, army, piece) -> 6
                    army == Army.WHITE -> line + 1
                    else -> line - 1
                }
            removePiece(col, startingPoint)
        }
        putPiece(col, line, this)
    }

    private fun convertPawn(move: String) {

    }

    override fun searchRoute(): MutableList<Pair<Coord, Boolean>?> {
        return getAllAvailableOptions()
    }

    private fun getAllAvailableOptions(): MutableList<Pair<Coord, Boolean>?> {
        val list = mutableListOf<Pair<Coord, Boolean>?>()
        val currPiece = board[col][line]
        val isWhite = currPiece?.army == Army.WHITE

        //if movement is up or down
        val lineDir = if (isWhite) -1 else 1
        // line in which pawn can move 2 spaces
        val moveTwo = if (isWhite) 6 else 1

        //check if pieces that can be captured exist
        if (line + lineDir in 0..7) {
            if (board[col][line + lineDir] == null) {
                list.add(Pair(Coord(col, line + lineDir), false))
                if (line == moveTwo && board[col][line + lineDir * 2] == null)
                    list.add(Pair(Coord(col, line + lineDir * 2), false))
            }
            checkCapture(col + 1, line + lineDir, currPiece?.army, list)
            checkCapture(col - 1, line + lineDir, currPiece?.army, list)
        }
        return list
    }

    private fun checkCapture(
        col: Int,
        line: Int,
        army: Army?,
        list: MutableList<Pair<Coord, Boolean>?>
    ) {
        if (col in 0..7 && board[col][line] != null && board[col][line]?.army != army)
            list.add((Pair(Coord(col, line), true)))
    }

    fun searchRouteToEat(): MutableList<Pair<Coord, Boolean>?> {
        val list = mutableListOf<Pair<Coord, Boolean>?>()
        var pair: Pair<Coord, Boolean>?
        val aux = if (army == Army.WHITE) -1 else 1
        pair = searchDirection(col - 1, line + aux)        //down left
        if (pair != null) {
            list.add((pair))
        }
        pair = searchDirection(col + 1, line + aux)       //down right
        if (pair != null) {
            list.add((pair))
        }
        return list
    }

    private fun searchDirection(col: Int, line: Int): Pair<Coord, Boolean>? {
        if (col !in 0..7 || line !in 0..7) {
            return null
        }
        //if (board[col][line] == null) {
            return Pair(Coord(col, line), false)
      //  }
      //  return null
    }

}