package pt.isel.pdm.chess4android

import pt.isel.pdm.chess4android.pieces.*

class GameModel() {

    var board: Array<Array<Piece?>> = Array(8) { Array<Piece?>(8) { null } }

    fun beginBoard() {
        //colocar as peças no estado inicial

        fillHalfBoard(0, getArmy(false))
        fillHalfBoard(7, getArmy(true))

        for (column in 0..7) {
            board[column][1] = Pawn(Army.BLACK)
            board[column][6] = Pawn(Army.WHITE)
        }
    }

    fun putPiece(col: Int, line: Int, piece: Piece) {
        board[col][line] = piece
    }

    fun removePiece(col: Int, line: Int) {
        board[col][line] = null
    }

    private fun fillHalfBoard(line: Int, army: Army) {
        for (column in 0..8) {
            when (column) {
                0, 7 -> board[column][line] = Rook(army)
                1, 6 -> board[column][line] = Knight(army)
                2, 5 -> board[column][line] = Bishop(army)
                3 -> board[column][line] = Queen(army)
                4 -> board[column][line] = King(army)
            }
        }
    }

    public fun checkIfPieceExists(col: Int, line: Int, army: Army, piece: PiecesType): Boolean {
        return (board[col][line] != null
                && board[col][line]?.army == army
                && board[col][line]?.piece == piece)
    }

    private fun castlingLeft() {
        //update Rook
        board[0][0] = null
        board[3][0] = Rook(Army.BLACK)

        //update King
        board[4][0] = null
        board[2][0] = King(Army.BLACK)
    }

    private fun castlingRight() {
        //update Rook
        board[7][7] = null
        board[5][7] = Rook(Army.WHITE)

        //update King
        board[4][7] = null
        board[6][7] = King(Army.WHITE)
    }

    fun placePieces(pgn: String) {
        beginBoard()
        var armyFlag = true
        val lst: List<String> = pgn.split(" ")
        for (move: String in lst) {
            val army = getArmy(armyFlag)
            when (move[0]) {
                'R' -> {
                    val rook = Rook(army)
                    rook.movePGN(move)
                }
                'B' -> {
                    val bishop = Bishop(army)
                    bishop.movePGN(move)
                }
                'Q' -> {
                    val queen = Queen(army)
                    queen.movePGN(move)
                }
                'N' -> {
                    val knight = Knight(army)
                    knight.movePGN(move)
                }
                'K' -> {
                    val king = King(army)
                    king.movePGN(move)
                }
                'O' -> {
                    if (move.length == 5)
                        castlingLeft()
                    else
                        castlingRight()
                }
                else -> {
                    val pawn = Pawn(army)
                    pawn.movePGN(move)
                }
            }
            armyFlag = !armyFlag
        }
    }

    fun getArmy(armyFlag: Boolean): Army {
        return if (armyFlag)
            Army.WHITE
        else
            Army.BLACK
    }
}