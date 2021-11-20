package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.Army
import pt.isel.pdm.chess4android.PiecesType

enum class KnightDir(val x: Int, val y: Int) {
    UP_LEFT(-1, -2),
    UP_RIGHT(1, -2),
    DOWN_LEFT(-1, 2),
    DOWN_RIGHT(1, 2),
    LEFT_UP(-2, -1),
    LEFT_DOWN(-2, 1),
    RIGHT_UP(2, -1),
    RIGHT_DOWN(2, 1)
}

class Knight(
    override var army: Army,
    override var board: Array<Array<Piece?>>,
    override var col: Int,
    override var line: Int
) : Piece() {

    override var piece = PiecesType.KNIGHT

    override fun movePGN(move: String) {
        var colDest: Int = 0
        var lineDest: Int = 0
        var startPositions: Pair<Int, Int> = Pair(-1, -1)
        when (move.length) {
            3 -> {
                colDest = move[1] - 'a'
                lineDest = 8 - move[2].digitToInt()
                startPositions = searchKnight(colDest, lineDest)
            }
            4 -> {
                if (move[1] == 'x') {
                    colDest = move[2] - 'a'
                    lineDest = 8 - move[3].digitToInt()
                    startPositions = searchKnight(colDest, lineDest)
                } else {
                    lineDest = 8 - move[3].digitToInt()
                    colDest = move[2] - 'a'
                    startPositions = if (move[1] in '0'..'9') {
                        val lineFrom = 8 - move[1].digitToInt()
                        Pair(getPrevCol(lineFrom, colDest, lineDest), lineFrom)
                    } else {
                        val colFrom = move[1] - 'a'
                        Pair(colFrom, getPrevLine(colFrom, colDest, lineDest, army))
                    }
                }

            }
            5 -> {
                colDest = move[3] - 'a'
                lineDest = 8 - move[4].digitToInt()
                startPositions = if (move[1] in '0'..'9') {
                    val lineFrom = 8 - move[1].digitToInt()
                    Pair(getPrevCol(lineFrom, colDest, lineDest), lineFrom)
                } else {
                    val colFrom = move[1] - 'a'
                    Pair(colFrom, getPrevLine(colFrom, colDest, lineDest, army))
                }
            }
        }

        removePiece(startPositions.first, startPositions.second)
        putPiece(colDest, lineDest, this)
    }

    private fun getPrevLine(colPrev: Int, colNext: Int, lineNext: Int, army: Army): Int {
        //we are able to get the diference in line by this subtraction
        when (colPrev - colNext) {
            -1, 1 -> {
                val pieceAux = board[colPrev][lineNext - 2]
                return if (lineNext - 2 in 0..7 &&
                    checkIfPieceExists(colPrev, lineNext - 2, army, piece)
                )
                    lineNext - 2
                else lineNext + 2
            }
            -2, 2 -> {
                return if (lineNext - 1 in 0..7 &&
                    checkIfPieceExists(colPrev, lineNext - 1, army, piece)
                )
                    lineNext - 1
                else lineNext + 1
            }
        }
        return 0
    }

    private fun getPrevCol(linePrev: Int, colNext: Int, lineNext: Int): Int {

        //we are able to get the diference in line by this subtraction
        when (linePrev - lineNext) {
            -1, 1 -> {
                val pieceAux = board[colNext - 2][linePrev]
                return if (colNext - 2 in 0..7 &&
                    checkIfPieceExists(colNext - 2, linePrev, army, piece)
                )
                    colNext - 2
                else colNext + 2
            }
            -2, 2 -> {
                val pieceAux = board[colNext - 1][linePrev]
                return if (colNext - 1 in 0..7 &&
                    checkIfPieceExists(colNext - 1, linePrev, army, piece)
                )
                    colNext - 1
                else colNext + 1
            }
        }
        return 0
    }

    private fun searchKnight(col: Int, line: Int): Pair<Int, Int> {
        for (dir in KnightDir.values()) {
            val x = col + dir.x
            val y = line + dir.y
            if (x in 0..7 && y in 0..7 && checkIfPieceExists(x, y, army, piece))
                return Pair(x, y)
        }
        return Pair(-1, -1)
    }


    override fun searchRoute(): MutableList<Pair<Coord, Boolean>?> {
        var list = mutableListOf<Pair<Coord, Boolean>?>()

        //up
        if (line - 2 in 0..7) {
            if (col + 1 in 0..7) {
                if (board[col + 1][line - 2] == null) list.add(
                    Pair(
                        Coord(col + 1, line - 2),
                        false
                    )
                )
                else if (board[col + 1][line - 2]?.army != army) list.add(
                    Pair(
                        Coord(
                            col + 1,
                            line - 2
                        ), true
                    )
                )
            }
            if (col - 1 in 0..7) {
                if (board[col - 1][line - 2] == null) list.add(
                    Pair(
                        Coord(col - 1, line - 2),
                        false
                    )
                )
                else if (board[col - 1][line - 2]?.army != army) list.add(
                    Pair(
                        Coord(
                            col - 1,
                            line - 2
                        ), true
                    )
                )
            }
        }

        //down
        if (line + 2 in 0..7) {
            if (col + 1 in 0..7) {
                if (board[col + 1][line + 2] == null) list.add(
                    Pair(
                        Coord(col + 1, line + 2),
                        false
                    )
                )
                else if (board[col + 1][line + 2]?.army != army) list.add(
                    Pair(
                        Coord(
                            col + 1,
                            line + 2
                        ), true
                    )
                )
            }


            if (col - 1 in 0..7) {
                if (board[col - 1][line + 2] == null) list.add(
                    Pair(
                        Coord(col - 1, line + 2),
                        false
                    )
                )
                else if (board[col - 1][line + 2]?.army != army) list.add(
                    Pair(
                        Coord(col - 1, line + 2),
                        true
                    )
                )

            }
        }

        //left
        if (col - 2 in 0..7) {
            if (line + 1 in 0..7) {
                if (board[col - 2][line + 1] == null) list.add(
                    Pair(
                        Coord(col - 2, line + 1),
                        false
                    )
                )
                else if (board[col - 2][line + 1]?.army != army) list.add(
                    Pair(
                        Coord(
                            col - 2,
                            line + 1
                        ), true
                    )
                )
            }
            if (line - 1 in 0..7) {
                if (board[col - 2][line - 1] == null) list.add(
                    Pair(
                        Coord(col - 2, line - 1),
                        false
                    )
                )
                else if (board[col - 2][line - 1]?.army != army) list.add(
                    Pair(
                        Coord(
                            col - 2,
                            line - 1
                        ), true
                    )
                )
            }
        }

        //right
        if (col + 2 in 0..7) {
            if (line + 1 in 0..7) {
                if (board[col + 2][line + 1] == null) list.add(
                    Pair(
                        Coord(col + 2, line + 1),
                        false
                    )
                )
                else if (board[col + 2][line + 1]?.army != army) list.add(
                    Pair(
                        Coord(
                            col + 2,
                            line + 1
                        ), true
                    )
                )
            }
            if (line - 1 in 0..7) {
                if (board[col + 2][line - 1] == null) list.add(
                    Pair(
                        Coord(col + 2, line - 1),
                        false
                    )
                )
                else if (board[col + 2][line - 1]?.army != army) list.add(
                    Pair(
                        Coord(
                            col + 2,
                            line - 1
                        ), true
                    )
                )
            }
        }

        return list
    }
}