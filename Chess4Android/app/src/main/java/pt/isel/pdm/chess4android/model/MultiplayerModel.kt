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

    fun getMoveOptions(col: Int, line: Int): MutableList<Pair<Coord, Boolean>?> {
        val piece = getPiece(col, line) ?: return mutableListOf()
        val routes = board[col][line]!!.searchRoute()
        return if (pieceChecking == null) routes
        else stopCheck(piece)

    }

    private fun stopCheck(pieceStopping: Piece): MutableList<Pair<Coord, Boolean>?> {
        //if(check normal)
        val routes = pieceStopping.searchRoute()
        if (pieceStopping.piece == PiecesType.KING)
            return routes

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


    fun signalCheck(piece: Piece, option: Pair<Coord, Boolean>?) {
        pieceChecking = piece
        checkPath = option
        (getPiece(option!!.first.col, option.first.line) as King).signalCheck(pieceChecking!!)
    }

    fun removeSignalCheck() {
        pieceChecking = null
        checkPath = null
        isDoubleCheck = false
    }

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
}