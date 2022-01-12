package pt.isel.pdm.chess4android.model

import pt.isel.pdm.chess4android.pieces.Coord
import pt.isel.pdm.chess4android.pieces.King
import pt.isel.pdm.chess4android.pieces.Piece

class MultiplayerModel : GameModel() {
    private var isDoubleCheck: Boolean = false

    fun switchArmy() {
        newArmyToPlay = if (newArmyToPlay == Army.WHITE) {
            Army.BLACK
        } else {
            Army.WHITE
        }
    }

    /**
     * Returns pieces routes if not check or is king
     * Else returns options to stop check (capture or block checking piece)
     */
    fun getMoveOptions(col: Int, line: Int): MutableList<Pair<Coord, Boolean>?> {
        val piece = getPiece(col, line) ?: return mutableListOf()
        val routes = board[col][line]!!.searchRoute()
        return if (piece.piece == PiecesType.KING || pieceChecking == null) {
            removeSignalCheck()
            routes
        } else stopCheck(piece)
    }

    private fun stopCheck(pieceStopping: Piece): MutableList<Pair<Coord, Boolean>?> {
        //if(check normal)
        val routes = pieceStopping.searchRoute()
        val blockCheckRoutes = mutableListOf<Pair<Coord, Boolean>?>()
        val king = getKing()
        //not possible to block
        when {
            isDoubleCheck -> return blockCheckRoutes

            pieceChecking!!.piece == PiecesType.KNIGHT -> {
                for (route in routes)
                    if (route!!.first.col == pieceChecking!!.col && route.first.line == pieceChecking!!.line) {
                        blockCheckRoutes.add(route)
                        break
                    }
            }
            isCheckDiagonal(pieceChecking!!) ->
                return getDiagonalBlockOptions(king!!, routes)

            isCheckHorizontal(pieceChecking!!) ->
                return getHorizontalBlockOptions(king!!, routes)

            isCheckVertical(pieceChecking!!) ->
                return getVerticalBlockOptions(king!!, routes)
        }
        return blockCheckRoutes
    }

    //else Double Check ?

    fun doubleCheck() {
        for (col in 0..7) {
            for (line in 0..7) {
                val piece = getPiece(col, line)
                if (piece?.army == newArmyToPlay && piece != pieceChecking) {
                    val routes = piece.searchRoute()
                    if (routes.isNotEmpty()) {
                        for (route in routes) {
                            val routeCoord = route!!.first
                            if (board[routeCoord.col][routeCoord.line] is King) {
                                isDoubleCheck = true
                                return
                            }
                        }
                    }
                }
            }
        }
        isDoubleCheck = false
    }

    fun signalCheck(piece: Piece, option: Pair<Coord, Boolean>?) {
        pieceChecking = piece
        checkPath = option
        (getPiece(option!!.first.col, option.first.line) as King).signalCheck(pieceChecking!!)
    }

    fun removeSignalCheck() {
        pieceChecking = null
        checkPath = null
        getKing()?.removeSignalCheck()
        isDoubleCheck = false
    }
}