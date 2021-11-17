package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.Army
import pt.isel.pdm.chess4android.PiecesType

class Bishop(override var army: Army) : Piece() {

    override var piece = PiecesType.BISHOP

    private fun searchBishop(tileStart: Int, army: Army): Pair<Int, Int> {
        var startColPosition = 0
        var startLinePosition = 0
        var initLineAux = tileStart - 1
        for (c in 0..MAX_BOARD_VAL) {
            initLineAux = if (initLineAux == 0) 1 else 0
            for (l in initLineAux until 8 step 2)
                if (board[c][l]?.second == PiecesType.BISHOP && board[c][l]?.first == army) {
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

}