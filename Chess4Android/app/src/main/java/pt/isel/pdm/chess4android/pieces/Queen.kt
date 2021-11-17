package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.Army
import pt.isel.pdm.chess4android.Pieces

class Queen(override var army: Army) : Piece() {

    override var piece = Pieces.QUEEN

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
            for (l in 0..8) {
                if (board[c][l]?.second == Piece.QUEEN && board[c][l]?.first == army) {
                    startColPosition = c
                    startLinePosition = l
                    break
                }
            }
        }
        return arrayOf(startColPosition, startLinePosition)
    }

}