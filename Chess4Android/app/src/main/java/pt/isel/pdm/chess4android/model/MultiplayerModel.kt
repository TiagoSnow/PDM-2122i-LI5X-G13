package pt.isel.pdm.chess4android.model

import android.util.Log
import pt.isel.pdm.chess4android.pieces.*

class MultiplayerModel : GameModel() {
    private var isDoubleCheck: Boolean = false
    private var localPlayerArmy: Army? = null

    fun switchArmy() {
        Log.v(
            "TEST",
            "My localPlayer is: " + localPlayerArmy + " and the next turn is " + newArmyToPlay.name
        )
        if (localPlayerArmy != null) return
        newArmyToPlay = if (newArmyToPlay == Army.WHITE) {
            Army.BLACK
        } else {
            Army.WHITE
        }
    }

    /**
     * Returns pieces routes if not check, is king or can be moved without compromising King
     * Else returns options to stop check (capture or block checking piece)
     */
    fun getMoveOptions(col: Int, line: Int): MutableList<Pair<Coord, Boolean>?> {
        val piece = getPiece(col, line) ?: return mutableListOf()
        return when {
            !canMovePiece(piece) -> mutableListOf()
            else -> {
                val routes = piece.searchRoute()
                if (piece.piece == PiecesType.KING || pieceChecking == null)
                    routes
                else stopCheck(piece)
            }
        }
    }

    /**
     *  Returns routes in which the selected [pieceStopping] can block the piece checking
     */
    private fun stopCheck(pieceStopping: Piece): MutableList<Pair<Coord, Boolean>?> {
        val routes = pieceStopping.searchRoute()
        val blockCheckRoutes = mutableListOf<Pair<Coord, Boolean>?>()
        val king = getKing()

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

    /**
     * Verification for the possibility of double check, which prevents movement of pieces that are not the King
     */
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

    fun verifyPiecePromotion(newCoord: Coord?): Boolean {
        if ((newCoord!!.line == 0 && board[newCoord.col][newCoord.line]!!.piece == PiecesType.PAWN
                    && board[newCoord.col][newCoord.line]!!.army == Army.WHITE)
            || (newCoord.line == 7 && board[newCoord.col][newCoord.line]!!.piece == PiecesType.PAWN
                    && board[newCoord.col][newCoord.line]!!.army == Army.BLACK)
        ) {
            return true
        }
        return false
    }

    fun promotePiece(newCoord: Coord?, pieceType: PiecesType) {
        val army = board[newCoord!!.col][newCoord.line]!!.army
        when (pieceType) {
            PiecesType.BISHOP -> board[newCoord.col][newCoord.line] =
                Bishop(army, board, newCoord.col, newCoord.line)
            PiecesType.KNIGHT -> board[newCoord.col][newCoord.line] =
                Knight(army, board, newCoord.col, newCoord.line)
            PiecesType.QUEEN -> board[newCoord.col][newCoord.line] =
                Queen(army, board, newCoord.col, newCoord.line)
            PiecesType.ROOK -> board[newCoord.col][newCoord.line] =
                Rook(army, board, newCoord.col, newCoord.line,moved = true)
            else -> {}
        }
    }

    fun isCheckMate(): Boolean {
        switchArmy()

        //king nao se poder mexer e estar em check

        //nenhuma peça pode fazer block
        for (col in 0..7)
            for (line in 0..7) {
                val piece = getPiece(col, line)
                if (piece != null && piece.army == newArmyToPlay) {
                    if (piece is King) {
                        val kingRoutes = piece.searchRoute()
                        if (kingRoutes.isNotEmpty()) {
                            switchArmy()
                            return false
                        }
                    } else {
                        val pieceRoutes = stopCheck(piece)
                        if (pieceRoutes.isNotEmpty()) {
                            switchArmy()
                            return false
                        }
                    }
                }
            }
        switchArmy()
        return true
    }

    fun setLocalPlayerArmy(localPlayer: Army) {
        localPlayerArmy = localPlayer
    }

    /**
     * Method that checks if piece can be moved without compromising the King
     * (Very ugly implementation :/ )
     */
    private fun canMovePiece(piece: Piece): Boolean {
        var listAux: MutableList<Pair<Coord, Boolean>?>
        for (col in 0..7) {
            for (line in 0..7) {
                val enemyPiece = board[col][line]
                if ((enemyPiece is Rook || enemyPiece is Queen || enemyPiece is Bishop)
                    && enemyPiece.army != piece.army
                ) {
                    val list1 = enemyPiece.searchRoute()
                    board[piece.col][piece.line] = null
                    listAux = enemyPiece.searchRoute()
                    if (listAux.size != 0)
                        if (!canCheckKing(list1, listAux, piece)) {
                            board[piece.col][piece.line] = piece
                            return false
                        }
                    board[piece.col][piece.line] = piece
                }
            }
        }
        board[piece.col][piece.line] = piece
        return true
    }

    private fun canCheckKing(
        optionsWithPiece: MutableList<Pair<Coord, Boolean>?>,
        optionsWithoutPiece: MutableList<Pair<Coord, Boolean>?>,
        piece: Piece
    ): Boolean {
        //
        val listAux = mutableListOf<Pair<Coord, Boolean>?>()
        val pair: Pair<Coord, Boolean> = Pair(Coord(piece.col, piece.line), false)
        val difference = optionsWithoutPiece.toSet().minus(optionsWithPiece.toSet())
        difference.forEach { enemyOption ->
            if (enemyOption != null && board[enemyOption.first.col][enemyOption.first.line]?.piece == PiecesType.KING)
                return false
        }
        return true
    }


    private fun isCheckDiagonal(pieceChecking: Piece): Boolean {
        return pieceChecking.col != checkPath!!.first.col && pieceChecking.line != checkPath!!.first.line
    }

    private fun getDiagonalBlockOptions(
        king: Piece,
        routes: MutableList<Pair<Coord, Boolean>?>
    ): MutableList<Pair<Coord, Boolean>?> {
        val blockCheckRoutes = mutableListOf<Pair<Coord, Boolean>?>()
        val xInc = if (pieceChecking?.col!! < king.col) 1 else -1
        val yInc = if (pieceChecking?.line!! < king.line) 1 else -1
        var x = pieceChecking!!.col
        var y = pieceChecking!!.line
        while (x != king.col && y != king.line) {
            routes.forEach { route ->
                if (route != null && route.first.col == x && route.first.line == y)
                    blockCheckRoutes.add(route)
            }
            x += xInc
            y += yInc
        }
        return blockCheckRoutes
    }

    private fun isCheckHorizontal(pieceChecking: Piece): Boolean {
        return pieceChecking.line == checkPath!!.first.line
    }

    private fun getHorizontalBlockOptions(
        king: Piece,
        routes: MutableList<Pair<Coord, Boolean>?>
    ): MutableList<Pair<Coord, Boolean>?> {
        val blockCheckRoutes = mutableListOf<Pair<Coord, Boolean>?>()
        val xInc = if (pieceChecking?.col!! < king.col) 1 else -1
        var x = pieceChecking!!.col
        while (x != king.col) {
            routes.forEach { route ->
                if (route != null && route.first.col == x && route.first.line == king.line)
                    blockCheckRoutes.add(route)
            }
            x += xInc
        }
        return blockCheckRoutes
    }

    private fun isCheckVertical(pieceChecking: Piece): Boolean {
        return pieceChecking.col == checkPath!!.first.col
    }

    private fun getVerticalBlockOptions(
        king: Piece,
        routes: MutableList<Pair<Coord, Boolean>?>
    ): MutableList<Pair<Coord, Boolean>?> {
        val blockCheckRoutes = mutableListOf<Pair<Coord, Boolean>?>()
        val yInc = if (pieceChecking?.line!! < king.line) 1 else -1
        var y = pieceChecking!!.line
        while (y != king.line) {
            routes.forEach { route ->
                if (route != null && route.first.line == y && route.first.col == king.col)
                    blockCheckRoutes.add(route)
            }
            y += yInc
        }
        return blockCheckRoutes
    }
}