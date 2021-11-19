package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.Army
import pt.isel.pdm.chess4android.PiecesType

class King(
    override var army: Army,
    override var board: Array<Array<Piece?>>,
    override var col: Int,
    override var line: Int
) : Piece() {

    override var piece = PiecesType.KING

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
        val piece = pt.isel.pdm.chess4android.PiecesType.KING

        if (col - 1 in 0..7 && line - 1 in 0..7)
            if (checkIfPieceExists(col - 1, line - 1, army, piece)) return Pair(
                col - 1,
                line - 1
            )      //diagonal up left

        if ((col in 0..7) && line - 1 in 0..7)
            if (checkIfPieceExists(col, line - 1, army, piece)) return Pair(
                col,
                line - 1
            )              //up

        if (col + 1 in 0..7 && line - 1 in 0..7)
            if (checkIfPieceExists(col + 1, line - 1, army, piece)) return Pair(
                col + 1,
                line - 1
            )      //diagonal up right

        if (col - 1 in 0..7 && line in 0..7)
            if (checkIfPieceExists(col - 1, line, army, piece)) return Pair(
                col - 1,
                line
            )              //left

        if (col + 1 in 0..7 && line in 0..7)
            if (checkIfPieceExists(col + 1, line, army, piece)) return Pair(
                col + 1,
                line
            )              //right

        if (col - 1 in 0..7 && line + 1 in 0..7)
            if (checkIfPieceExists(col - 1, line + 1, army, piece)) return Pair(
                col - 1,
                line + 1
            )      //diagonal down left

        if ((col in 0..7) && line + 1 in 0..7)
            if (checkIfPieceExists(col, line + 1, army, piece)) return Pair(
                col,
                line + 1
            )             //down

        if (col + 1 in 0..7 && line + 1 in 0..7)
            if (checkIfPieceExists(col + 1, line + 1, army, piece)) return Pair(
                col + 1,
                line + 1
            )     //diagonal down right

        return Pair(-1, -1);
    }

    override fun searchRoute(): MutableList<Pair<Coord, Boolean>?> {
        //move-se para todos os lados. Somente uma casa

        val allOptionsKing = getAllAvailableOptions()

        val interceptionList = getAllAvailableOptionsFromEnemy(allOptionsKing)

        return interceptionList.toMutableList()

    }

    fun interception(allOptionsKing: MutableList<Pair<Coord, Boolean>?>, allEnemyOptions: MutableList<Pair<Coord, Boolean>?>): MutableList<Pair<Coord, Boolean>?> {
        var listAux = mutableListOf<Pair<Coord, Boolean>?>()
        for (option in allOptionsKing) {
            for (enemyOptions in allEnemyOptions) {
                if (pairIsEqual(option, enemyOptions))
                    listAux.add(option)
            }
        }
        if(listAux.size != 0) {
            for (pair in listAux) {
                allOptionsKing.remove(pair)
            }
        }
        return allOptionsKing
    }

    private fun pairIsEqual(kingOption: Pair<Coord, Boolean>?, enemyOption: Pair<Coord, Boolean>?): Boolean {
        if (kingOption!!.first.col == enemyOption!!.first.col &&
            kingOption.first.line == enemyOption.first.line &&
            kingOption.second == enemyOption.second) {
            return true
        }
        return false
    }

    private fun getAllAvailableOptionsFromEnemy(allOptionsKing: MutableList<Pair<Coord, Boolean>?>): MutableSet<Pair<Coord, Boolean>?> {
        var listAux: MutableList<Pair<Coord, Boolean>?>
        var list = mutableSetOf<Pair<Coord, Boolean>?>()
        for (col in 0..7) {
            for (line in 0..7) {
                val curr = board[col][line]
                if (curr !is King) {
                    if (curr != null && curr.army != army) {
                        if (curr is Pawn) {
                            listAux = curr.searchRouteToEat()
                        } else {
                            listAux = curr.searchRoute()
                        }
                        if (listAux.size != 0) {
                            list = updateList(interception(allOptionsKing, listAux), list)
                        }
                    }
                }
            }
        }
        return list
    }

    private fun updateList(
        interceptionList: MutableList<Pair<Coord, Boolean>?>,
        listAux: MutableSet<Pair<Coord, Boolean>?>
    ): MutableSet<Pair<Coord, Boolean>?> {
        //first Time
        if(listAux.size == 0) {
            return interceptionList.toMutableSet()
        }

        if(interceptionList.size < listAux.size) {
            return interceptionList.toMutableSet()
        }
        return listAux
    }

    private fun getAllAvailableOptions(): MutableList<Pair<Coord, Boolean>?> {
        val list = mutableListOf<Pair<Coord, Boolean>?>()

        //up
        var pair = searchKingDirection(col, line - 1)
        if(pair != null) {
            list.add(pair)
        }

        //left
        pair = searchKingDirection(col - 1, line)
        if(pair != null) {
            list.add(pair)
        }

        //right
        pair = searchKingDirection(col + 1, line)
        if(pair != null) {
            list.add(pair)
        }

        //down
        pair = searchKingDirection(col, line + 1)
        if(pair != null) {
            list.add(pair)
        }

        //diagonal up/left
        pair = searchKingDirection(col - 1, line - 1)
        if(pair != null) {
            list.add(pair)
        }

        //diagonal up/right
        pair = searchKingDirection(col + 1, line - 1)
        if(pair != null) {
            list.add(pair)
        }

        //diagonal down/left
        pair = searchKingDirection(col - 1, line + 1)
        if(pair != null) {
            list.add(pair)
        }

        //diagonal down/right
        pair = searchKingDirection(col + 1, line + 1)
        if(pair != null) {
            list.add(pair)
        }

        return list
    }

    private fun searchKingDirection(col: Int, line: Int): Pair<Coord, Boolean>? {
        if (col !in 0..7 || line !in 0..7) {
            return null
        }
        if (board[col][line]?.army != army) {
            if (board[col][line] == null)
                return Pair(Coord(col, line), false)
            return Pair(Coord(col, line), true)
        }
        return null
    }
}