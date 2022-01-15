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
        var moveTemp = move
        val col: Int
        val line: Int
        var startingPoint = 0
        var toConvert: Char? = null

        //TODO UPGRADE PAWN
        if ('=' in moveTemp) {
            toConvert = if ('x' in moveTemp) {
                moveTemp[5]
            } else {
                moveTemp[4]
            }
            moveTemp = moveTemp.split('=')[0]
        }

        if (moveTemp.length == 4) {
            col = moveTemp[2] - 'a'
            line = 8 - moveTemp[3].digitToInt()
            val startColumn = moveTemp[0] - 'a'
            for (l in 0..7) {
                if (checkIfPieceExists(startColumn, l, army, piece)) {
                    startingPoint = l
                    break
                }
            }
            removePiece(startColumn, startingPoint)
        } else {
            col = moveTemp[0] - 'a'
            line = 8 - moveTemp[1].digitToInt()
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
            
            removePiece(col, startingPoint)
        }
        if (toConvert == null) {
            putPiece(col, line, this)
        } else {
            val piece: Piece = when (toConvert) {
                'N' -> Knight(army, board, col, line)
                'Q' -> Queen(army, board, col, line)
                'R' -> Rook(army, board, col, line, false)
                'B' -> Bishop(army, board, col, line)
                else -> this
            }
            putPiece(col, line, piece)
        }
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
        if (col in 0..7 && line in 0..7) {
            return Pair(Coord(col, line), false)
        }
        return null
    }
}