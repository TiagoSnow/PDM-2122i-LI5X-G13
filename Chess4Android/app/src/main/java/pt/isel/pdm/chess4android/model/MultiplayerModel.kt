package pt.isel.pdm.chess4android.model

import pt.isel.pdm.chess4android.pieces.Coord
import pt.isel.pdm.chess4android.pieces.Piece

class MultiplayerModel : GameModel() {

    fun switchArmy() {
        newArmyToPlay = if (newArmyToPlay == Army.WHITE) {
            Army.BLACK
        } else {
            Army.WHITE
        }
    }

    fun getMoveOptions(col: Int, line: Int): MutableList<Pair<Coord, Boolean>?>? {
        val piece = getPiece(col, line) ?: return mutableListOf()
        val routes = board[col][line]?.searchRoute()
        return if (pieceChecking == null) routes
        else stopCheck(piece)

    }

    private fun stopCheck(
        pieceStopping: Piece
    ): MutableList<Pair<Coord, Boolean>?>? {
        //if(check normal)
        val routes = pieceStopping.searchRoute()
        val blockCheckRoutes = mutableListOf<Pair<Coord, Boolean>?>()
        val king = getKing()
        //trocar para checking
        if (pieceStopping.piece == PiecesType.KNIGHT) {
            routes.forEach { route ->
                if (board[route!!.first.col][route.first.line] == pieceChecking)
                    blockCheckRoutes.add(route)
                if (route.)
            }

        } else {
            if (isCheckDiagonal(pieceChecking!!, king!!))
                for (option in getDiagonalBlockOptions(king, routes)) {
                    blockCheckRoutes.add(option)
                }
            //else if (isCheckHorizontal)


            /*routes.forEach { route ->
                if (board[route!!.first.col][route.first.line] == pieceChecking) {
                    blockCheckRoutes.add(route)
                } else {

                }
                */

        }
        return blockCheckRoutes
    }


    //else


    fun signalCheck(piece: Piece, option: Pair<Coord, Boolean>?) {
        pieceChecking = piece
        checkPath = option
    }

    fun removeSignalCheck() {
        pieceChecking = null
        checkPath = null
    }

}