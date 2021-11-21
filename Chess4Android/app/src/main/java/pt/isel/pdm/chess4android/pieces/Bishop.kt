package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.Army
import pt.isel.pdm.chess4android.PiecesType

class Bishop(
    override var army: Army,
    override var board: Array<Array<Piece?>>,
    override var col: Int,
    override var line: Int
) : Piece() {

    override var piece = PiecesType.BISHOP

    private fun searchBishop(tileStart: Int, army: Army): Pair<Int, Int> {
        var startColPosition = 0
        var startLinePosition = 0
        var initLineAux = tileStart - 1
        for (c in 0..MAX_BOARD_VAL) {
            initLineAux = if (initLineAux == 0) 1 else 0
            for (l in initLineAux until 8 step 2)
                if (board[c][l]?.piece == PiecesType.BISHOP && board[c][l]?.army == army) {
                    startColPosition = c
                    startLinePosition = l
                    break
                }
        }
        return Pair(startColPosition, startLinePosition)
    }

    override fun movePGN(move: String) {
        val col: Int
        val line: Int
        if (move.length == 3) {
            col = move[1] - 'a'
            line = 8 - move[2].digitToInt()
        } else {
            col = move[2] - 'a'
            line = 8 - move[3].digitToInt()
        }
        val startPositions = searchBishop((col + line) % 2, army)//tile => 0 = white, 1 = black

        removePiece(startPositions.first, startPositions.second)
        putPiece(col, line, this)
    }

    override fun searchRoute(): MutableList<Pair<Coord, Boolean>?> {
        return getAllAvailableOptions()
    }

     private fun getAllAvailableOptions(): MutableList<Pair<Coord, Boolean>?> {
        val list = mutableListOf<Pair<Coord, Boolean>?>()

        val listDigUL = searchRouteDiagonal(-1, -1)     //diagonal up/left
        val listDigUR = searchRouteDiagonal(+1, -1)   //diagonal up/right
        val listDL = searchRouteDiagonal(-1, +1)  //diagonal down/left
        val listDR = searchRouteDiagonal(+1, +1)   //diagonal down/right

        list.addAll(listDigUL)
        list.addAll(listDigUR)
        list.addAll(listDL)
        list.addAll(listDR)

        return list
    }

    private fun searchRouteDiagonal(colDir: Int, lineDir: Int): MutableList<Pair<Coord, Boolean>?> {
        val list = mutableListOf<Pair<Coord, Boolean>?>()
        var colDirection = colDir
        var lineDirection = lineDir

        while (col + colDirection in 0..7 && line + lineDirection in 0..7 && board[col + colDirection][line + lineDirection]?.army != army) {
            if (board[col + colDirection][line + lineDirection] == null) list.add(
                Pair(
                    Coord(
                        col + colDirection,
                        line + lineDirection
                    ), false
                )
            )
            else {
                list.add(Pair(Coord(col + colDirection, line + lineDirection), true))
                break
            }
            colDirection += colDir
            lineDirection += lineDir
        }
        return list
    }
}