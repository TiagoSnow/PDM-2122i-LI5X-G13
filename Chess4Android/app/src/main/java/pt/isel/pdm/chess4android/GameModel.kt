package pt.isel.pdm.chess4android

import pt.isel.pdm.chess4android.pieces.*

class GameModel() {

    private var pieceChecking: Piece? = null
    private var checkingFrom: Coord? = null
    private var isInCheck = false
    var newArmyToPlay: Army = Army.WHITE
    var board: Array<Array<Piece?>> = Array(8) { Array<Piece?>(8) { null } }

    private fun beginBoard() {
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

    private fun castlingLeft(armyFlag: Boolean) {
        if(armyFlag) {
            board[0][7] = null
            board[3][7] = Rook(Army.WHITE, board, 3, 7)
            //update King
            board[4][7] = null
            board[2][7] = King(Army.WHITE, board, 2, 7)
        }
        else {
            board[0][0] = null
            board[3][0] = Rook(Army.BLACK, board, 3, 0)
            //update King
            board[4][0] = null
            board[2][0] = King(Army.BLACK, board, 2, 0)
        }
    }

    private fun castlingRight(armyFlag: Boolean) {
        if(armyFlag){
            board[7][7] = null
            board[5][7] = Rook(Army.WHITE, board, 5, 7)

            //update King
            board[4][7] = null
            board[6][7] = King(Army.WHITE, board, 6, 7)
        }
        else {
            //update Rook
            board[7][0] = null
            board[5][0] = Rook(Army.BLACK, board, 5, 0)

            //update King
            board[4][0] = null
            board[6][0] = King(Army.BLACK, board, 6, 0)
        }
    }

    var lastPGNMoveCol = 0
    var lastPGNMoveLine = 0

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
                    lastPGNMoveCol = rook.col
                    lastPGNMoveLine = rook.line
                }
                'B' -> {
                    val bishop = Bishop(army, board, 0, 0)
                    bishop.movePGN(move)
                    lastPGNMoveCol = bishop.col
                    lastPGNMoveLine = bishop.line
                }
                'Q' -> {
                    val queen = Queen(army, board, 0, 0)
                    queen.movePGN(move)
                    lastPGNMoveCol = queen.col
                    lastPGNMoveLine = queen.line
                }
                'N' -> {
                    val knight = Knight(army, board, 0, 0)
                    knight.movePGN(move)
                    lastPGNMoveCol = knight.col
                    lastPGNMoveLine = knight.line
                }
                'K' -> {
                    val king = King(army, board, 0, 0)
                    king.movePGN(move)
                    lastPGNMoveCol = king.col
                    lastPGNMoveLine = king.line
                }
                'O' -> {
                    if (move.length == 5)
                        castlingLeft(armyFlag)
                    else
                        castlingRight(armyFlag)
                }
                else -> {
                    val pawn = Pawn(army, board, 0, 0)
                    pawn.movePGN(move)
                    lastPGNMoveCol = pawn.col
                    lastPGNMoveLine = pawn.line
                }
            }
            armyFlag = !armyFlag
        }
        newArmyToPlay = getArmy(armyFlag)
    }

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

    private fun getArmy(armyFlag: Boolean): Army {
        return if (armyFlag)
            Army.WHITE
        else
            Army.BLACK
    }

    fun getPiece(column: Int, row: Int): Piece? {
        return board[column][row]
    }

    private fun updateCheckingPiece(selectedPiece: Piece?) {
        pieceChecking = selectedPiece

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


    fun stopPieceFromMoving(piece: Piece): Boolean {
        //true -> não se pode mexer
        //false -> pode-se mexer
        for (col in 0..7) {
            for (line in 0..7) {
                if (board[col][line]?.army == newArmyToPlay && board[col][line] != pieceChecking) {
                    board[piece.col][piece.line] = null
                    var path = board[col][line]?.searchRoute()
                    if (path != null) {
                        for (option in path) {
                            if (board[option!!.first.col][option.first.line] is King) {
                                board[piece.col][piece.line] = piece
                                return true
                            }
                        }
                    }
                }
            }
        }
        board[piece.col][piece.line] = piece
        return false
    }
}