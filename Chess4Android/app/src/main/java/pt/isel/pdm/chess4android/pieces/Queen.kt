package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.Army
import pt.isel.pdm.chess4android.PiecesType

class Queen(
    override var army: Army,
    override var board: Array<Array<Piece?>>,
    override var col: Int,
    override var line: Int
) : Piece() {

    override var piece = PiecesType.QUEEN

    override fun movePGN(move: String) {
        val col: Int
        val line: Int
        if (move.length == 3) {
            col = move[1] - 'a'
            line = 8 - move[2].digitToInt()
        } else {
            col = move[2] - 'a'
            line = 8 - move[3].digitToInt()
        }

        val startPositions = searchQueen(army)

        removePiece(startPositions[0], startPositions[1])
        putPiece(col, line, this)
    }

    private fun searchQueen(army: Army): Array<Int> {
        var startColPosition = 0
        var startLinePosition = 0
        for (c in 0..7) {
            for (l in 0..7) {
                if (board[c][l]?.piece == PiecesType.QUEEN && board[c][l]?.army == army) {
                    startColPosition = c
                    startLinePosition = l
                    break
                }
            }
        }
        return arrayOf(startColPosition, startLinePosition)
    }

    override fun searchRoute(): MutableList<Pair<Coord, Boolean>?> {
        return getAllAvailableOptions()
    }

    private fun getAllAvailableOptions(): MutableList<Pair<Coord, Boolean>?> {
        var list = getAllAvailableOptionsBishop()
        list.addAll(getAllAvailableOptionsRook())
        return list
    }

    private fun getAllAvailableOptionsRook(): MutableList<Pair<Coord, Boolean>?> {
        val list = mutableListOf<Pair<Coord, Boolean>?>()

        val listUp = searchRouteLine(0)             //up
        val listLeft = searchRouteColumn(0)       //left
        val listRight = searchRouteColumn(7)        //right
        val listDown = searchRouteLine(7)         //down


        list.addAll(listUp)
        list.addAll(listLeft)
        list.addAll(listRight)
        list.addAll(listDown)

        return list
    }

    private fun getAllAvailableOptionsBishop(): MutableList<Pair<Coord, Boolean>?> {
        val list = mutableListOf<Pair<Coord, Boolean>?>()

        val listDigUL = searchRouteDiagonal(-1, -1)     //diagonal up/left
        val listDigUR = searchRouteDiagonal(+1, -1)   //diagonal up/right
        val listDL = searchRouteDiagonal(-1, +1)  //diagonal down/left
        val listDR = searchRouteDiagonal(+1, +1)   //diagonal down/right

        list.addAll(listDigUL)
        list.addAll(listDigUR)
        list.addAll(listDL)
        list.addAll(listDR)

        return list
    }

    private fun searchRouteDiagonal(colDir: Int, lineDir: Int): MutableList<Pair<Coord, Boolean>?> {
        val list = mutableListOf<Pair<Coord, Boolean>?>()
        var colDirection = colDir
        var lineDirection = lineDir

        while (col + colDirection in 0..7 && line + lineDirection in 0..7 && board[col + colDirection][line + lineDirection]?.army != army) {
            if (board[col + colDirection][line + lineDirection] == null) list.add(
                Pair(
                    Coord(
                        col + colDirection,
                        line + lineDirection
                    ), false
                )
            )
            else list.add(Pair(Coord(col + colDirection, line + lineDirection), true))
            colDirection += colDir
            lineDirection += lineDir
        }
        return list
    }

    private fun searchRouteLine(direction: Int): MutableList<Pair<Coord, Boolean>?> {
        var array = mutableListOf<Pair<Coord, Boolean>?>()

        if (line > direction) {     //up
            for (lineAux in (line - 1) downTo direction) {
                val position = board[col][lineAux]
                if (position == null) {
                    array.add(Pair(Coord(col, lineAux), false))
                }

                if (position is Piece) {
                    if (position.army == army)
                        break
                    else {
                        array.add(Pair(Coord(col, lineAux), true))
                        break
                    }
                }
            }
        } else {              //down
            for (lineAux in (line + 1)..direction) {
                val position = board[col][lineAux]
                if (position == null) {
                    array.add(Pair(Coord(col, lineAux), false))
                }

                if (position is Piece) {
                    if (position.army == army)
                        break
                    else {
                        array.add(Pair(Coord(col, lineAux), true))
                        break
                    }
                }
            }
        }
        return array
    }

    private fun searchRouteColumn(direction: Int): MutableList<Pair<Coord, Boolean>?> {
        var array = mutableListOf<Pair<Coord, Boolean>?>()

        if (col > direction) {     //left
            for (colAux in (col - 1) downTo 0) {
                val position = board[colAux][line]
                if (position == null) {
                    array.add(Pair(Coord(colAux, line), false))
                }
                if (position is Piece) {
                    if (position.army == army)
                        break
                    else {
                        array.add(Pair(Coord(colAux, line), true))
                        break
                    }
                }
            }
        } else {                      //right
            for (colAux in (col + 1)..direction) {
                val position = board[colAux][line]
                if (position == null) {
                    array.add(Pair(Coord(colAux, line), false))
                }
                if (position is Piece) {
                    if (position.army == army)
                        break
                    else {
                        array.add(Pair(Coord(colAux, line), true))
                        break
                    }
                }
            }
        }
        return array
    }
}