package pt.isel.pdm.chess4android

import pt.isel.pdm.chess4android.pieces.Coord
import pt.isel.pdm.chess4android.pieces.King
import pt.isel.pdm.chess4android.pieces.Piece

class ChessLogic {
    private var pieceChecking: Piece? = null
    private var checkingFrom: Coord? = null
    private var isInCheck = false

    var board: Array<Array<Piece?>> = Array(8) { Array<Piece?>(8) { null } }


    fun check(column: Int, line: Int): MutableList<Coord> {
        val selectedPiece = board[column][line]
        val allOptions = selectedPiece?.searchRoute()
        allOptions?.removeIf { x -> !x!!.second }
        val toRet = mutableListOf<Coord>()
        if (allOptions != null) {
            for (option in allOptions) {
                val coord = option!!.first
                val possibleKing = board[coord.col][coord.line]
                if (possibleKing?.army != selectedPiece.army && possibleKing?.piece == PiecesType.KING) {
                    updateCheckingPiece(selectedPiece)
                    checkingFrom = Coord(column, line)
                    toRet.add(Coord(possibleKing.col, possibleKing.line))
                    toRet.add(Coord(column, line))
                    isInCheck = true
                    //addPathToKing(Pair(column, line), coord, possibleKing.army == Army.WHITE)
                }
            }
        }
        return toRet
    }



    fun getOptionsToBlockCheck(
        piece: Piece,
        checkOptions: MutableList<Coord>
    ): MutableList<Pair<Coord, Boolean>?> {
        val paths = piece.searchRoute()
        val pathsChecking = pieceChecking?.searchRoute()!!
        val king: King = board[checkOptions[0].col][checkOptions[0].line] as King
        val kingMoves = king.standardMoves()
        val toRet = mutableListOf<Pair<Coord, Boolean>?>()
        if (piece is King) return paths
        for (option in paths) {
            if (option!!.second && pieceChecking == board[option.first.col][option.first.line]) {
                toRet.add(option)
            }
            for (blockOption in kingMoves) {
                if (option.first.col == blockOption!!.first.col &&
                    option.first.line == blockOption.first.line
                ) {
                    for (checking in pathsChecking) {
                        if (option.first.col == checking!!.first.col &&
                            option.first.line == checking.first.line
                        )
                            if (option.first.col == king.col &&
                                option.first.col == checkOptions[1].col ||
                                option.first.line == king.line &&
                                option.first.line == checkOptions[1].line
                            )
                                toRet.add(option)
                    }
                }
            }
        }
        return toRet
    }


    private fun updateCheckingPiece(selectedPiece: Piece?) {
        pieceChecking = selectedPiece
    }


}