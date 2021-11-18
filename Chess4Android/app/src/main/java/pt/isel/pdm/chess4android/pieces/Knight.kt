package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.Army
import pt.isel.pdm.chess4android.PiecesType

class Knight(
    override var army: Army,
    override var board: Array<Array<Piece?>>,
    override var col: Int,
    override var line: Int
) : Piece() {

    override var piece = PiecesType.KNIGHT

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
        val startPositions = searchKnight(col, line, army)

        removePiece(startPositions.first, startPositions.second)
        putPiece(col, line, this)
    }

    private fun searchKnight(col: Int, line: Int, army: Army): Pair<Int, Int> {
        if (col + 2 in 0..7) {
            if (line + 1 in 0..7 && checkIfPieceExists(
                    col + 2,
                    line + 1,
                    army,
                    pt.isel.pdm.chess4android.PiecesType.KNIGHT
                )
            ) return Pair(col + 2, line + 1)
            if (line - 1 in 0..7 && checkIfPieceExists(
                    col + 2,
                    line - 1,
                    army,
                    pt.isel.pdm.chess4android.PiecesType.KNIGHT
                )
            ) return Pair(col + 2, line - 1)
        }
        if (col - 2 in 0..7) {
            if (line + 1 in 0..7 && checkIfPieceExists(
                    col - 2,
                    line + 1,
                    army,
                    pt.isel.pdm.chess4android.PiecesType.KNIGHT
                )
            ) return Pair(col - 2, line + 1)
            if (line - 1 in 0..7 && checkIfPieceExists(
                    col - 2,
                    line - 1,
                    army,
                    pt.isel.pdm.chess4android.PiecesType.KNIGHT
                )
            ) return Pair(col - 2, line - 1)
        }
        if (line + 2 in 0..7) {
            if (col + 1 in 0..7 && checkIfPieceExists(
                    col + 1,
                    line + 2,
                    army,
                    pt.isel.pdm.chess4android.PiecesType.KNIGHT
                )
            ) return Pair(col + 1, line + 2)
            if (col - 1 >= 0 && checkIfPieceExists(
                    col - 1,
                    line + 2,
                    army,
                    pt.isel.pdm.chess4android.PiecesType.KNIGHT
                )
            ) return Pair(col - 1, line + 2)
        }
        if (line - 2 in 0..7) {
            if (col + 1 in 0..7 && checkIfPieceExists(
                    col + 1,
                    line - 2,
                    army,
                    pt.isel.pdm.chess4android.PiecesType.KNIGHT
                )
            ) return Pair(col + 1, line - 2)
            if (col - 1 in 0..7 && checkIfPieceExists(
                    col - 1,
                    line - 2,
                    army,
                    pt.isel.pdm.chess4android.PiecesType.KNIGHT
                )
            ) return Pair(col - 1, line - 2)
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