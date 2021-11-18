package pt.isel.pdm.chess4android

import pt.isel.pdm.chess4android.pieces.*

class GameModel() {

    var board: Array<Array<Piece?>> = Array(8) { Array<Piece?>(8) { null } }

    fun beginBoard() {
        //colocar as peças no estado inicial

        fillHalfBoard(0, getArmy(false))
        fillHalfBoard(7, getArmy(true))

        for (column in 0..7) {
            board[column][1] = Pawn(Army.BLACK, board)
            board[column][6] = Pawn(Army.WHITE, board)
        }
    }

    private fun fillHalfBoard(line: Int, army: Army) {
        for (column in 0..7) {
            when (column) {
                0, 7 -> board[column][line] = Rook(army, board)
                1, 6 -> board[column][line] = Knight(army, board)
                2, 5 -> board[column][line] = Bishop(army, board)
                3 -> board[column][line] = Queen(army, board)
                4 -> board[column][line] = King(army, board)
            }
        }
    }

    private fun castlingLeft() {
        //update Rook
        board[0][0] = null
        board[3][0] = Rook(Army.BLACK, board)

        //update King
        board[4][0] = null
        board[2][0] = King(Army.BLACK, board)
    }

    private fun castlingRight() {
        //update Rook
        board[7][7] = null
        board[5][7] = Rook(Army.WHITE, board)

        //update King
        board[4][7] = null
        board[6][7] = King(Army.WHITE, board)
    }

    fun placePieces(pgn: String, board: Array<Array<Piece?>>): Array<Array<Piece?>> {
        this.board = board
        beginBoard()
        var armyFlag = true
        val lst: List<String> = pgn.split(" ")
        for (move: String in lst) {
            val army = getArmy(armyFlag)
            when (move[0]) {
                'R' -> {
                    val rook = Rook(army, board)
                    rook.movePGN(move)
                }
                'B' -> {
                    val bishop = Bishop(army, board)
                    bishop.movePGN(move)
                }
                'Q' -> {
                    val queen = Queen(army, board)
                    queen.movePGN(move)
                }
                'N' -> {
                    val knight = Knight(army, board)
                    knight.movePGN(move)
                }
                'K' -> {
                    val king = King(army, board)
                    king.movePGN(move)
                }
                'O' -> {
                    if (move.length == 5)
                        castlingLeft()
                    else
                        castlingRight()
                }
                else -> {
                    val pawn = Pawn(army, board)
                    pawn.movePGN(move)
                }
            }
            armyFlag = !armyFlag
        }
        return board
    }

    fun getArmy(armyFlag: Boolean): Army {
        return if (armyFlag)
            Army.WHITE
        else
            Army.BLACK
    }
}