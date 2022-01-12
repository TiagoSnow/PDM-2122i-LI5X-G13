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
    override var line: Int

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
            if (col + dir.x in 0..7 && line + dir.y in 0..7)
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

        if (pieceChecking != null)
            return stopCheckAsKing(interceptionList)

        return interceptionList
    }


    private fun pairIsEqual(
        kingOption: Pair<Coord, Boolean>?,
        enemyOption: Pair<Coord, Boolean>?
    ): Boolean {
        return (kingOption!!.first.col == enemyOption!!.first.col &&
                kingOption.first.line == enemyOption.first.line
                )
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

    fun standardMoves(): MutableList<Pair<Coord, Boolean>?> {
        val list = mutableListOf<Pair<Coord, Boolean>?>()
        for (dir in KingDir.values())
            list.add(Pair(Coord(col + dir.x, line + dir.y), false))
        return list
    }

    private fun searchKingDirection(col: Int, line: Int): Pair<Coord, Boolean>? {
        if (col !in 0..7 || line !in 0..7) {
            return null
        }
        if (board[col][line]?.army != army) {
            return if (board[col][line] == null) Pair(Coord(col, line), false) else Pair(
                Coord(
                    col,
                    line
                ), true
            )
        }
        return null
    }

    private fun canEatCheckingPiece(route: Pair<Coord, Boolean>): Boolean {
        val a = mutableListOf<Pair<Coord, Boolean>?>()
        a.add(route)
        return getAllAvailableOptionsFromEnemy(a).isEmpty()
    }

    private fun stopCheckAsKing(routes: MutableList<Pair<Coord, Boolean>?>): MutableList<Pair<Coord, Boolean>?> {
        for (route in routes) {
            if (route!!.first.col == pieceChecking?.col && route.first.line == pieceChecking?.line) {
                if (!canEatCheckingPiece(route))
                    routes.remove(route)
                break
            }
        }
        pieceChecking = null
        return routes
    }

    fun signalCheck(pieceChecking: Piece) {
        this.pieceChecking = pieceChecking
    }
}