package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.model.Army
import pt.isel.pdm.chess4android.model.PiecesType

enum class BishopDir(val x: Int, val y: Int) {
    UP_LEFT(-1, -1),
    UP_RIGHT(1, -1),
    DOWN_LEFT(-1, 1),
    DOWN_RIGHT(1, 1),
}

class Bishop(
    override var army: Army,
    override var board: Array<Array<Piece?>>,
    override var col: Int,
    override var line: Int
) : Piece() {

    override var piece = PiecesType.BISHOP

    private fun searchBishop(tileStart: Int, army: Army): Pair<Int, Int> {
        var startColPosition = MIN_BOARD
        var startLinePosition = MIN_BOARD
        var initLineAux = tileStart - 1
        for (c in MIN_BOARD..MAX_BOARD) {
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
        for (dir in BishopDir.values()) {
            val route = searchRouteDiagonal(dir.x, dir.y)
            list.addAll(route)
        }
        return list
    }

    private fun searchRouteDiagonal(colDir: Int, lineDir: Int): MutableList<Pair<Coord, Boolean>?> {
        return searchDiagonal(colDir, lineDir, col, line, army, board)
    }
}