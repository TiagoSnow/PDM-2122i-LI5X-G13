package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.Army
import pt.isel.pdm.chess4android.PiecesType

class Rook(
    override var army: Army,
    override var board: Array<Array<Piece?>>,
    override var col: Int,
    override var line: Int
) : Piece() {


    override var piece = PiecesType.ROOK

    override fun movePGN(move: String) {
        var colDest = 0
        var lineDest = 0
        var colFrom = 0
        var lineFrom = 0
        when (move.length) {
            3 -> {
                //Destination values
                colDest = move[1] - 'a'
                lineDest = 8 - move[2].digitToInt()
                val coords = searchRook(colDest, lineDest, army)
                colFrom = coords.first
                lineFrom = coords.second
            }
            4 -> {
                colDest = move[2] - 'a'
                lineDest = 8 - move[3].digitToInt()
                // If capture
                if (move[1] == 'x') {
                    //Rxc7
                    val coords = searchRook(colDest, lineDest, army)
                    colFrom = coords.first
                    lineFrom = coords.second
                } else {
                    if (move[1] in '0'..'9') {      //R3c4
                        lineFrom = 8 - move[1].digitToInt()
                        colFrom = colDest
                    } else {                        //Rhc3
                        colFrom = move[1] - 'a'
                        //specific case where the column stays the same in vertical move (ex:Raa3)
                        lineFrom = searchRookInSameCol(colFrom, colDest, lineDest, army)
                    }
                }
            }
            5 -> { //R4xc5 || Rdxd5
                colDest = move[3] - 'a'
                lineDest = 8 - move[4].digitToInt()
                if (move[1] in '0'..'9') {
                    colFrom = colDest
                    lineFrom = 8 - move[1].digitToInt()
                } else {
                    colFrom = move[1] - 'a'
                    lineFrom = searchRookInSameCol(colFrom, colDest, lineDest, army)
                }
            }
        }
        removePiece(colFrom, lineFrom)
        putPiece(colDest, lineDest, this)
    }

    private fun searchRook(colDest: Int, lineDest: Int, army: Army): Pair<Int, Int> {
        var colFrom = 0
        var lineFrom = 0
        for (i in 1..7) {
            if (lineDest + i <= MAX_BOARD_VAL && checkIfPieceExists(
                    colDest,
                    lineDest + i,
                    army,
                    PiecesType.ROOK
                )
            ) {
                colFrom = colDest
                lineFrom = lineDest + i
                break
            }
            //left search
            if (lineDest - i >= MIN_BOARD_VAL && checkIfPieceExists(
                    colDest,
                    lineDest - i,
                    army,
                    PiecesType.ROOK
                )
            ) {
                colFrom = colDest
                lineFrom = lineDest - i
                break
            }
            //searches for rook in previous position vertically
            //right search
            if (colDest + i <= MAX_BOARD_VAL && checkIfPieceExists(
                    colDest + i,
                    lineDest,
                    army,
                    PiecesType.ROOK
                )
            ) {
                colFrom = colDest + i
                lineFrom = lineDest
                break
            }
            //left search
            if (colDest - i >= MIN_BOARD_VAL && checkIfPieceExists(
                    colDest - i,
                    lineDest,
                    army,
                    PiecesType.ROOK
                )
            ) {
                colFrom = colDest - i
                lineFrom = lineDest
                break
            }
        }
        return Pair(colFrom, lineFrom)
    }

    private fun searchRookInSameCol(
        colFrom: Int,
        colDest: Int,
        lineDest: Int,
        army: Army,
    ): Int {
        return if (colFrom == colDest) {
            var line = 0
            while (line < MAX_BOARD_VAL) {
                if (checkIfPieceExists(colFrom, line, army, PiecesType.ROOK))
                    break
                line++
            }
            line
        } else lineDest
    }

    override fun searchRoute(): MutableList<Pair<Coord, Boolean>?> {
        //anda para todos os lados

        //procura nas casas mais proximas
        return getAllAvailableOptions()

        //se a tile à frente tiver vazia adicionamos ao array
        //se tiver à frente uma piece da mesma cor, return
        //se tiver à frente uma piece de cor diferente, adiciona ao array com flag
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

    private fun getAllAvailableOptions(): MutableList<Pair<Coord, Boolean>?> {
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
}