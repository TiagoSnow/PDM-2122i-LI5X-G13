package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.model.Army
import pt.isel.pdm.chess4android.model.PiecesType

internal fun searchHorizontal(
    direction: Int,
    col: Int,
    line: Int,
    army: Army,
    board: Array<Array<Piece?>>
): MutableList<Pair<Coord, Boolean>?> {
    val array = mutableListOf<Pair<Coord, Boolean>?>()

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
                    if (lineAux - 1 >= 0 && position.piece == PiecesType.KING)
                        array.add(Pair(Coord(col, lineAux - 1), true))
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
                    if (lineAux + 1 >= 0 && position.piece == PiecesType.KING)
                        array.add(Pair(Coord(col, lineAux + 1), true))
                    break
                }
            }
        }
    }
    return array
}

internal fun searchDiagonal(
    colDir: Int, lineDir: Int, col: Int,
    line: Int,
    army: Army,
    board: Array<Array<Piece?>>
): MutableList<Pair<Coord, Boolean>?> {
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
        else {
            list.add(Pair(Coord(col + colDirection, line + lineDirection), true))
            if (col + colDirection + colDir in 0..7 && line + lineDirection + lineDir in 0..7 && board[col + colDirection][line + lineDirection]?.piece == PiecesType.KING)
                list.add(
                    Pair(
                        Coord(
                            col + colDirection + colDir,
                            line + lineDirection + lineDir
                        ), false
                    )
                )

            break
        }
        colDirection += colDir
        lineDirection += lineDir
    }
    return list
}

internal fun searchVertical(
    direction: Int,
    col: Int,
    line: Int,
    army: Army,
    board: Array<Array<Piece?>>
): MutableList<Pair<Coord, Boolean>?> {
    val array = mutableListOf<Pair<Coord, Boolean>?>()

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
                    if (colAux - 1 >= 0 && position.piece == PiecesType.KING)
                        array.add(Pair(Coord(colAux - 1, line), true))
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
                    if (colAux + 1 >= 0 && position.piece == PiecesType.KING)
                        array.add(Pair(Coord(colAux + 1, line), true))
                    break
                }
            }
        }
    }
    return array
}




