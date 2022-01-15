package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.model.Army
import pt.isel.pdm.chess4android.model.PiecesType

enum class KingDir(val x: Int, val y: Int) {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0),
    UP_LEFT(-1, -1),
    UP_RIGHT(1, -1),
    DOWN_LEFT(-1, 1),
    DOWN_RIGHT(1, 1),
}

class King(
    override var army: Army,
    override var board: Array<Array<Piece?>>,
    override var col: Int,
    override var line: Int,
    var moved: Boolean

) : Piece() {

    override var piece = PiecesType.KING
    var pieceChecking: Piece? = null
    override fun movePGN(move: String) {
        val col: Int
        val line: Int

        if (move.length == 4) {
            col = move[2] - 'a'
            line = 8 - move[3].digitToInt()
        } else {
            col = move[1] - 'a'
            line = 8 - move[2].digitToInt()
        }
        val startPositions = searchKing(col, line, army)

        removePiece(startPositions.first, startPositions.second)
        putPiece(col, line, this)
    }

    private fun searchKing(col: Int, line: Int, army: Army): Pair<Int, Int> {
        for (dir in KingDir.values()) {
            if (col + dir.x in MIN_BOARD..MAX_BOARD && line + dir.y in MIN_BOARD..MAX_BOARD)
                if (checkIfPieceExists(col + dir.x, line + dir.y, army, piece)) return Pair(
                    col + dir.x,
                    line + dir.y
                )
        }
        return Pair(-1, -1)
    }

    override fun searchRoute(): MutableList<Pair<Coord, Boolean>?> {
        val allOptionsKing = getAllAvailableOptions()

        val interceptionList = getAllAvailableOptionsFromEnemy(allOptionsKing)

        return if (pieceChecking != null) stopCheckAsKing(interceptionList)
        else interceptionList
    }

    private fun getAllAvailableOptionsFromEnemy(allOptionsKing: MutableList<Pair<Coord, Boolean>?>): MutableList<Pair<Coord, Boolean>?> {
        var listAux: MutableList<Pair<Coord, Boolean>?>
        var list = allOptionsKing
        for (col in 0..7) {
            for (line in 0..7) {
                val curr = board[col][line]
                if (curr !is King && curr != null && curr.army != army) {
                    listAux = if (curr is Pawn) {
                        curr.searchRouteToEat()
                    } else {
                        curr.searchRoute()
                    }
                    if (listAux.size != 0)
                        list = interception(allOptionsKing, listAux)

                }
            }
        }
        return list
    }

    private fun interception(
        allOptionsKing: MutableList<Pair<Coord, Boolean>?>,
        allEnemyOptions: MutableList<Pair<Coord, Boolean>?>
    ): MutableList<Pair<Coord, Boolean>?> {
        //
        val listAux = mutableListOf<Pair<Coord, Boolean>?>()
        for (option in allOptionsKing) {
            for (enemyOptions in allEnemyOptions) {
                if (pairIsEqual(option, enemyOptions)) {
                    listAux.add(option)
                    break
                }
            }
        }
        if (listAux.size != 0) {
            for (pair in listAux) {
                allOptionsKing.remove(pair)
            }
        }
        return allOptionsKing
    }

    private fun pairIsEqual(
        option: Pair<Coord, Boolean>?,
        enemyOptions: Pair<Coord, Boolean>?
    ): Boolean {
        if (option != null && enemyOptions != null) {
            return option.first.col == enemyOptions.first.col && option.first.line == enemyOptions.first.line
        }
        return false
    }

    private fun getAllAvailableOptions(): MutableList<Pair<Coord, Boolean>?> {
        val list = mutableListOf<Pair<Coord, Boolean>?>()
        for (dir in KingDir.values()) {
            val pair = searchKingDirection(col + dir.x, line + dir.y)
            if (pair != null) {
                list.add(pair)
            }
        }
        return list
    }

    private fun searchKingDirection(col: Int, line: Int): Pair<Coord, Boolean>? {
        if (col !in 0..7 || line !in 0..7) {
            return null
        }
        return when {
            board[col][line]?.army != army -> {
                if (board[col][line] == null) Pair(Coord(col, line), false)
                else Pair(Coord(col, line), true)
            }
            else -> null
        }
    }

    private fun canEatCheckingPiece(route: Pair<Coord, Boolean>): Boolean {
        return canBeEaten(route)
    }

    private fun canBeEaten(route: Pair<Coord, Boolean>): Boolean {
        //mudar para a posição de comer. Ver o que retorna o searchRoute() e apagar previamente o pieceChecking
        val currKing = board[col][line]
        val currRoutePiece = board[route.first.col][route.first.line]

        //update king in board
        val currCol = col
        val currLine = line
        board[route.first.col][route.first.line] = currKing
        board[col][line] = null

        //update the king coords
        val newKing: King = board[route.first.col][route.first.line] as King
        newKing.col = route.first.col
        newKing.line = route.first.line

        //delete pieceChecking
        val prevpieceChecking = pieceChecking
        pieceChecking = null

        val listAux = mutableListOf<Pair<Coord, Boolean>?>()
        listAux.add(route)
        val list = newKing.getAllAvailableOptionsFromEnemy(listAux)

        //reset board
        col = currCol
        line = currLine
        board[col][line] = currKing
        board[route.first.col][route.first.line] = currRoutePiece
        pieceChecking = prevpieceChecking

        return list.isNotEmpty()
    }

    private fun stopCheckAsKing(routes: MutableList<Pair<Coord, Boolean>?>): MutableList<Pair<Coord, Boolean>?> {
        for (route in routes) {
            if (route!!.first.col == pieceChecking?.col && route.first.line == pieceChecking?.line) {
                if (!canEatCheckingPiece(route))
                    routes.remove(route)
                break
            }
        }
        return routes
    }

    fun signalCheck(pieceChecking: Piece) {
        this.pieceChecking = pieceChecking
    }

    fun removeSignalCheck() {
        this.pieceChecking = null
    }


    private fun castlingLeft(): Coord? {        //returns avaiable position or null
        val yCoord = if (army == Army.BLACK) 7 else 0

        val piece: Piece? = board[0][yCoord]
        val king: Piece? = board[4][yCoord]

        if ((piece != null && piece is Rook && !piece.moved)       //Se é Rook e ainda não se mexeu
            && (king != null && king is King && !king.moved)      //Se é King e ainda não se mexeu
            && checkIfPathIsSafeToCastling(                        //caminho não tem peças, nem options enemy
                yCoord,
                dir = "left"
            )
            && king.pieceChecking == null                          //King não está em check
        )
            return Coord(0, yCoord)

        return null
    }

    private fun castlingRight(): Coord? {

        val yCoord = if (army == Army.BLACK) 7 else 0

        val piece: Piece? = board[7][yCoord]
        val king: Piece? = board[4][yCoord]


        if ((piece != null && piece is Rook && !piece.moved)       //Se é Rook e ainda não se mexeu
            && (king != null && king is King && !king.moved)      //Se é King e ainda não se mexeu
            && checkIfPathIsSafeToCastling(                       //caminho não tem peças, nem options enemy
                yCoord,
                dir = "right"
            )
            && king.pieceChecking == null   //King não está em check
        )
            return Coord(7, yCoord)

        return null
    }

    private fun checkIfPathIsSafeToCastling(yCoord: Int, dir: String): Boolean {

        val listPosToCastling: MutableList<Pair<Coord, Boolean>?> =
            if (dir == "left") {
                if (board[1][yCoord] == null) {
                    mutableListOf(Pair(Coord(2, yCoord), false), Pair(Coord(3, yCoord), false))
                } else mutableListOf()
            } else {
                mutableListOf(Pair(Coord(5, yCoord), false), Pair(Coord(6, yCoord), false))
            }

        if (listPosToCastling.isEmpty()) return false    //posicao do Cavalo (1,yCoord) está ocupada

        val listEnemyOption: MutableList<Pair<Coord, Boolean>?> =
            getAllAvailableOptionsFromEnemy(listPosToCastling)

        for (option in listEnemyOption) {
            if (board[option!!.first.col][option.first.line] != null) {
                return false
            }
        }
        return true
    }


}