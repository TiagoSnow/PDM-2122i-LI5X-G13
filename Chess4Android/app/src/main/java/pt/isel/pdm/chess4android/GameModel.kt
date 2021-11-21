package pt.isel.pdm.chess4android

import pt.isel.pdm.chess4android.pieces.*
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt

class GameModel() {

    private var pieceThatMadeCheck: Piece? = null
    var newArmyToPlay: Army = Army.WHITE
    var board: Array<Array<Piece?>> = Array(8) { Array<Piece?>(8) { null } }

    fun beginBoard() {
        //colocar as peças no estado inicial
        fillHalfBoard(0, getArmy(false))
        fillHalfBoard(7, getArmy(true))

        for (column in 0..7) {
            board[column][1] = Pawn(Army.BLACK, board, column, 1)
            board[column][6] = Pawn(Army.WHITE, board, column, 6)
        }
    }

    private fun fillHalfBoard(line: Int, army: Army) {
        for (column in 0..7) {
            when (column) {
                0, 7 -> board[column][line] = Rook(army, board, column, line)
                1, 6 -> board[column][line] = Knight(army, board, column, line)
                2, 5 -> board[column][line] = Bishop(army, board, column, line)
                3 -> board[column][line] = Queen(army, board, column, line)
                4 -> board[column][line] = King(army, board, column, line)
            }
        }
    }

    private fun castlingLeft() {
        //update Rook
        board[0][0] = null
        board[3][0] = Rook(Army.BLACK, board, 3, 0)

        //update King
        board[4][0] = null
        board[2][0] = King(Army.BLACK, board, 2, 0)
    }

    private fun castlingRight() {
        //update Rook
        board[7][7] = null
        board[5][7] = Rook(Army.WHITE, board, 5, 7)

        //update King
        board[4][7] = null
        board[6][7] = King(Army.WHITE, board, 6, 7)
    }

    fun placePieces(pgn: String, board: Array<Array<Piece?>>) {
        this.board = board
        beginBoard()
        var armyFlag = true
        val lst: List<String> = pgn.split(" ")
        for (move: String in lst) {
            val army = getArmy(armyFlag)
            when (move[0]) {
                'R' -> {
                    val rook = Rook(
                        army,
                        board,
                        0,
                        0
                    )      //this coordinates is only to initialize the object. Coordinates will be updated at the end of the function
                    rook.movePGN(move)
                }
                'B' -> {
                    val bishop = Bishop(army, board, 0, 0)
                    bishop.movePGN(move)
                }
                'Q' -> {
                    val queen = Queen(army, board, 0, 0)
                    queen.movePGN(move)
                }
                'N' -> {
                    val knight = Knight(army, board, 0, 0)
                    knight.movePGN(move)
                }
                'K' -> {
                    val king = King(army, board, 0, 0)
                    king.movePGN(move)
                }
                'O' -> {
                    if (move.length == 5)
                        castlingLeft()
                    else
                        castlingRight()
                }
                else -> {
                    val pawn = Pawn(army, board, 0, 0)
                    pawn.movePGN(move)
                }
            }
            armyFlag = !armyFlag
        }
        newArmyToPlay = getArmy(armyFlag)
    }

    fun check(column: Int, line: Int): MutableList<Pair<Coord, Boolean>> {
        val selectedPiece = board[column][line]
        val allOptions = selectedPiece!!.searchRoute()
        for (option in allOptions) {
            val coord = option!!.first
            val possibleKing = board[coord.col][coord.line]
            if (possibleKing?.army != selectedPiece.army && possibleKing?.piece == PiecesType.KING) {
                return getRouteToKing(selectedPiece, possibleKing, allOptions)
            }
        }
        return mutableListOf()
    }

    private fun getRouteToKing(
        selectedPiece: Piece,
        king: Piece,
        newOptions: MutableList<Pair<Coord, Boolean>?>
    ): MutableList<Pair<Coord, Boolean>> {

        var listAux = mutableListOf<Pair<Coord, Double>>()
        var list = mutableListOf<Pair<Coord, Boolean>>()
        val selectedCoord = Coord(selectedPiece.col, selectedPiece.line)
        val kingCoord = Coord(king.col, king.line)
        val distanceSelectedToKing = distanceBetweenTwoPositions(selectedCoord, kingCoord)

        for (option in newOptions) {
            val currCoord = option!!.first
            val currDist = distanceBetweenTwoPositions(currCoord, kingCoord)
            if(currDist <= distanceSelectedToKing)
                listAux.add(Pair(currCoord, currDist))
        }

        if(selectedPiece is Knight) {
            listAux.sortBy { pair -> pair.second }
            list.add(Pair(listAux[0].first, false))
            return list
        }

        if(selectedPiece is Rook) {
            for (pair in listAux) {
                list.add(Pair(pair.first, false))
            }
            return list
        }

        var distanceInsertedToSelect = distanceSelectedToKing
        var pair = chooseBestOptionFromList(listAux, king)
        while(pair!!.second <= distanceInsertedToSelect) {
            listAux.remove(pair)
            list.add(Pair(pair.first, false))
            distanceInsertedToSelect = pair.second
            pair = chooseBestOptionFromList(listAux, king)
            if(pair == null) return list
        }
        return list
    }

    private fun chooseBestOptionFromList(listAux: MutableList<Pair<Coord, Double>>, king: Piece): Pair<Coord, Double>? {
        if(listAux.isEmpty()) return null
        var best = listAux[0]
        var bestColDist = distanceBetweenTwoPositions(Coord(best.first.col,0), Coord(king.col,0))
        var bestLineDist = distanceBetweenTwoPositions(Coord(0,best.first.line), Coord(0,king.line))

        for (option in listAux) {
            val opCoord = option.first
            val opDist = option.second
            if (opDist < best.second) {
                val colDist = distanceBetweenTwoPositions(Coord(opCoord.col,0), Coord(king.col,0))
                val lineDist = distanceBetweenTwoPositions(Coord(0,opCoord.line), Coord(0,king.line))
                if ((colDist - lineDist).absoluteValue < (bestColDist - bestLineDist).absoluteValue) {
                    best = option
                    bestColDist = colDist
                    bestLineDist = lineDist
                }
            }
        }
        return best
    }

    private fun distanceBetweenTwoPositions(initPosition: Coord, endPosition: Coord): Double {
        return sqrt(
            (endPosition.col.toDouble() - initPosition.col).pow(2.0) +
                    (endPosition.line.toDouble() - initPosition.line).pow(2.0)
        )
    }

    fun getArmy(armyFlag: Boolean): Army {
        return if (armyFlag)
            Army.WHITE
        else
            Army.BLACK
    }

    fun getOptionsToBlockCheck(col: Int, line: Int, checkOptions: MutableList<Pair<Coord, Boolean>>): MutableList<Pair<Coord, Boolean>?> {
        val selectedPiece = board[col][line]
        val allOptions = selectedPiece!!.searchRoute()
        var interceptionList = mutableListOf<Pair<Coord, Boolean>?>()
        for (option in allOptions) {
            val optionCoord = option!!.first
            if(pieceThatMadeCheck!!.col == optionCoord.col && pieceThatMadeCheck!!.line == optionCoord.line) {
                interceptionList.add(Pair(Coord(pieceThatMadeCheck!!.col, pieceThatMadeCheck!!.line), true))
            } else {
                for (checkOption in checkOptions) {
                    val checkCoord = checkOption.first
                    if (optionCoord.col == checkCoord.col && optionCoord.line == checkCoord.line) {
                        interceptionList.add(option)
                    }
                }
            }
        }
        return interceptionList
    }

    fun updatePieceBeganCheck(coord: Coord?) {
        if(coord != null) {
            pieceThatMadeCheck = board[coord.col][coord.line]!!
        }
        else {
            pieceThatMadeCheck = null
        }

    }
}