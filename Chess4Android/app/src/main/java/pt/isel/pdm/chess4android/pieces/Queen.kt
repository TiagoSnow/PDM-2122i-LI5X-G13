package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.model.Army
import pt.isel.pdm.chess4android.model.PiecesType

class Queen(
    override var army: Army,
    override var board: Array<Array<Piece?>>,
    override var col: Int,
    override var line: Int
) : Piece() {

    override var piece = PiecesType.QUEEN

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

        val startPositions = searchQueen(army)

        removePiece(startPositions[0], startPositions[1])
        putPiece(col, line, this)
    }

    private fun searchQueen(army: Army): Array<Int> {
        var startColPosition = 0
        var startLinePosition = 0
        for (c in 0..7) {
            for (l in 0..7) {
                if (board[c][l]?.piece == PiecesType.QUEEN && board[c][l]?.army == army) {
                    startColPosition = c
                    startLinePosition = l
                    break
                }
            }
        }
        return arrayOf(startColPosition, startLinePosition)
    }

    override fun searchRoute(): MutableList<Pair<Coord, Boolean>?> {
        return getAllAvailableOptions()
    }

    private fun getAllAvailableOptions(): MutableList<Pair<Coord, Boolean>?> {
        return mutableListOf<Pair<Coord, Boolean>?>().apply {
            addAll(searchRouteDiagonal(-1, -1)  )
            addAll(searchRouteDiagonal(+1, -1) )
            addAll(searchRouteDiagonal(-1, +1))
            addAll(searchRouteDiagonal(+1, +1))

            addAll(searchRouteLine(0))
            addAll(searchRouteColumn(0))
            addAll(searchRouteColumn(7))
            addAll(searchRouteLine(7))
        }
    }

    private fun searchRouteDiagonal(colDir: Int, lineDir: Int): MutableList<Pair<Coord, Boolean>?> {
        return searchDiagonal(colDir, lineDir, col, line, army, board)
    }

    private fun searchRouteLine(direction: Int): MutableList<Pair<Coord, Boolean>?> {
        return searchHorizontal(direction, col, line, army, board)
    }

    private fun searchRouteColumn(direction: Int): MutableList<Pair<Coord, Boolean>?> {
        return searchVertical(direction, col, line, army, board)
    }
}