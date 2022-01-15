package pt.isel.pdm.chess4android.model

import pt.isel.pdm.chess4android.pieces.*


open class GameModel() {
    var newArmyToPlay: Army = Army.WHITE
    var board: Array<Array<Piece?>> = Array(BOARD_SIZE) { Array<Piece?>(BOARD_SIZE) { null } }

    lateinit var solutions: ArrayList<Pair<Coord, Coord>>

    protected var checkPath: Pair<Coord, Boolean>? = null
    protected var pieceChecking: Piece? = null

    /**
     * Initial board setup
     */
    fun beginBoard() {
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

    /**
     * Resolve Castling in PGN
     */
    protected fun castlingLeft(armyFlag: Boolean) {
        if (armyFlag) {
            board[0][7] = null
            board[3][7] = Rook(Army.WHITE, board, 3, 7)
            //update King
            board[4][7] = null
            board[2][7] = King(Army.WHITE, board, 2, 7)
        } else {
            board[0][0] = null
            board[3][0] = Rook(Army.BLACK, board, 3, 0)
            //update King
            board[4][0] = null
            board[2][0] = King(Army.BLACK, board, 2, 0)
        }
    }

    protected fun castlingRight(armyFlag: Boolean) {
        val yCoord = if (armyFlag) 7 else 0
        val army = if (armyFlag) Army.WHITE else Army.BLACK

        board[7][yCoord] = null
        board[5][yCoord] = Rook(army, board, 5, yCoord)

        //update King
        board[4][yCoord] = null
        board[6][yCoord] = King(army, board, 6, yCoord)

    }

    var lastPGNMoveCol = 0
    var lastPGNMoveLine = 0

    protected fun getArmy(armyFlag: Boolean): Army {
        return if (armyFlag)
            Army.WHITE
        else
            Army.BLACK
    }

    fun getPiece(column: Int, row: Int): Piece? {
        return board[column][row]
    }

    fun movePiece(prevCoord: Coord?, newCoord: Coord?) {
        val prevCol = prevCoord!!.col
        val prevLine = prevCoord.line
        val newCol = newCoord!!.col
        val newLine = newCoord.line

        val movedPiece = board[prevCol][prevLine]

        //Atualizar coords da peça movida
        movedPiece?.col = newCol
        movedPiece?.line = newLine

        //Atualizar model
        board[newCol][newLine] = movedPiece
        board[prevCol][prevLine] = null
    }

    fun isChecking(option: Pair<Coord, Boolean>?): Boolean {
        return board[option!!.first.col][option.first.line]?.piece == PiecesType.KING
    }

    protected fun getKing(): King? {
        for (line in board)
            for (elem in line)
                if (elem != null && elem.piece == PiecesType.KING && elem.army == newArmyToPlay)
                    return elem as King
        return null
    }

}