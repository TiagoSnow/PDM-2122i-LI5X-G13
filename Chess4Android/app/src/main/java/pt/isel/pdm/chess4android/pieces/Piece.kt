package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.model.Army
import pt.isel.pdm.chess4android.model.PiecesType

abstract class Piece {

    val MIN_BOARD = 0
    val MAX_BOARD = 7
    abstract val board: Array<Array<Piece?>>

    abstract var col: Int
    abstract var line: Int

    abstract var piece: PiecesType
    abstract val army: Army
    abstract fun movePGN(move: String)

    fun checkIfPieceExists(col: Int, line: Int, army: Army, piece: PiecesType): Boolean {
        return (board[col][line] != null
                && board[col][line]?.army == army
                && board[col][line]?.piece == piece)
    }

    fun putPiece(col: Int, line: Int, piece: Piece) {
        board[col][line] = piece
        board[col][line]?.col = col
        board[col][line]?.line = line

    }

    fun removePiece(col: Int, line: Int) {
        board[col][line] = null
    }

    fun updateBoard(colDest: Int, lineDest: Int) {
        removePiece(this.col, this.line)
        this.col = colDest
        this.line = lineDest
        putPiece(this.col, this.line, this)
    }

    abstract fun searchRoute(): MutableList<Pair<Coord, Boolean>?>




    fun pairIsEqual(
        kingOption: Pair<Coord, Boolean>?,
        enemyOption: Pair<Coord, Boolean>?
    ): Boolean {
        return (kingOption!!.first.col == enemyOption!!.first.col &&
                kingOption.first.line == enemyOption.first.line
                )
    }

}