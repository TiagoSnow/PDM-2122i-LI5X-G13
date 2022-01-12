package pt.isel.pdm.chess4android.model

import pt.isel.pdm.chess4android.pieces.*

open class GameModel() {
    var newArmyToPlay: Army = Army.WHITE
    var board: Array<Array<Piece?>> = Array(8) { Array<Piece?>(8) { null } }

    //var isChecking: Boolean = false // to delete?
    lateinit var solutions: ArrayList<Pair<Coord, Coord>>

    //private var options: MutableList<Pair<Coord, Boolean>?>? = null
    protected var checkPath: Pair<Coord, Boolean>? = null
    protected var pieceChecking: Piece? = null

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

    protected fun isCheckDiagonal(pieceChecking: Piece): Boolean {
        return pieceChecking.col != checkPath!!.first.col && pieceChecking.line != checkPath!!.first.line
    }

    protected fun getDiagonalBlockOptions(
        king: Piece,
        routes: MutableList<Pair<Coord, Boolean>?>
    ): MutableList<Pair<Coord, Boolean>?> {
        val blockCheckRoutes = mutableListOf<Pair<Coord, Boolean>?>()
        val xInc = if (pieceChecking?.col!! < king.col) 1 else -1
        val yInc = if (pieceChecking?.line!! < king.line) 1 else -1
        var x = pieceChecking!!.col
        var y = pieceChecking!!.line
        while (x != king.col && y != king.line) {
            routes.forEach { route ->
                if (route != null && route.first.col == x && route.first.line == y)
                    blockCheckRoutes.add(route)
            }
            x += xInc
            y += yInc
        }
        return blockCheckRoutes
    }

    protected fun isCheckHorizontal(pieceChecking: Piece): Boolean {
        return pieceChecking.line == checkPath!!.first.line
    }

    protected fun getHorizontalBlockOptions(
        king: Piece,
        routes: MutableList<Pair<Coord, Boolean>?>
    ): MutableList<Pair<Coord, Boolean>?> {
        val blockCheckRoutes = mutableListOf<Pair<Coord, Boolean>?>()
        val xInc = if (pieceChecking?.col!! < king.col) 1 else -1
        var x = pieceChecking!!.col
        while (x != king.col) {
            routes.forEach { route ->
                if (route != null && route.first.col == x && route.first.line == king.line)
                    blockCheckRoutes.add(route)
            }
            x += xInc
        }
        return blockCheckRoutes
    }

    protected fun isCheckVertical(pieceChecking: Piece): Boolean {
        return pieceChecking.col == checkPath!!.first.col
    }

    protected fun getVerticalBlockOptions(
        king: Piece,
        routes: MutableList<Pair<Coord, Boolean>?>
    ): MutableList<Pair<Coord, Boolean>?> {
        val blockCheckRoutes = mutableListOf<Pair<Coord, Boolean>?>()
        val yInc = if (pieceChecking?.line!! < king.line) 1 else -1
        var y = pieceChecking!!.line
        while (y != king.line) {
            routes.forEach { route ->
                if (route != null && route.first.line == y && route.first.col == king.col)
                    blockCheckRoutes.add(route)
            }
            y += yInc
        }
        return blockCheckRoutes
    }


    protected fun getKing(): King? {
        for (line in board)
            for (elem in line)
                if (elem != null && elem.piece == PiecesType.KING && elem.army == newArmyToPlay)
                    return elem as King
        return null
    }

}