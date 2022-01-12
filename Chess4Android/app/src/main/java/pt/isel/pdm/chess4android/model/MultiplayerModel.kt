package pt.isel.pdm.chess4android.model

import pt.isel.pdm.chess4android.pieces.*

class MultiplayerModel : GameModel() {

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
    }

    fun verifyPiecePromotion(newCoord: Coord?): Boolean {
        if((newCoord!!.line==0 && board[newCoord!!.col][newCoord.line]!!.piece == PiecesType.PAWN
            && board[newCoord!!.col][newCoord.line]!!.army == Army.WHITE)
            || (newCoord!!.line==7 && board[newCoord!!.col][newCoord.line]!!.piece == PiecesType.PAWN
                    && board[newCoord!!.col][newCoord.line]!!.army == Army.BLACK)
                ){
           return true
        }
        return false
    }

    fun promotePiece(newCoord: Coord?, pieceType: PiecesType) {
        var army = board[newCoord!!.col][newCoord.line]!!.army
        when(pieceType){
            PiecesType.BISHOP -> board[newCoord!!.col][newCoord.line] = Bishop(army,board,newCoord.col,newCoord.line)
            PiecesType.KNIGHT -> board[newCoord!!.col][newCoord.line] = Knight(army,board,newCoord.col,newCoord.line)
            PiecesType.QUEEN -> board[newCoord!!.col][newCoord.line] = Queen(army,board,newCoord.col,newCoord.line)
            PiecesType.ROOK -> board[newCoord!!.col][newCoord.line] = Rook(army,board,newCoord.col,newCoord.line)
        }
    }
}