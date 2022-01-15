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

    fun canMovePiece(
        pieceOptions: MutableList<Pair<Coord, Boolean>?>,
    ): Boolean {
        var listAux: MutableList<Pair<Coord, Boolean>?>
        var list = pieceOptions
        for (col in 0..7) {
            for (line in 0..7) {
                val enemyPiece = board[col][line]
                if ((enemyPiece is Rook || enemyPiece is Queen || enemyPiece is Bishop)
                    && enemyPiece.army != army
                ) {
                    val list1 = enemyPiece.searchRoute()
                    board[this.col][this.line] = null
                    listAux = enemyPiece.searchRoute()
                    if (listAux.size != 0)
                        if (!interception2(list1, listAux))
                            return false
                }
            }
        }
        board[this.col][this.line] = this
        return true
    }

    private fun interception2(
        optionsWithPiece: MutableList<Pair<Coord, Boolean>?>,
        optionsWithoutPiece: MutableList<Pair<Coord, Boolean>?>
    ): Boolean {
        //
        val listAux = mutableListOf<Pair<Coord, Boolean>?>()
        val pair: Pair<Coord, Boolean> = Pair(Coord(this.col, this.line), false)
        val difference = optionsWithoutPiece.toSet().minus(optionsWithPiece.toSet())
        difference.forEach { enemyOption ->
            if (enemyOption != null && board[enemyOption.first.col][enemyOption.first.line]?.piece == PiecesType.KING)
                return false
        }
        return true
    }


    fun pairIsEqual(
        kingOption: Pair<Coord, Boolean>?,
        enemyOption: Pair<Coord, Boolean>?
    ): Boolean {
        return (kingOption!!.first.col == enemyOption!!.first.col &&
                kingOption.first.line == enemyOption.first.line
                )
    }

}