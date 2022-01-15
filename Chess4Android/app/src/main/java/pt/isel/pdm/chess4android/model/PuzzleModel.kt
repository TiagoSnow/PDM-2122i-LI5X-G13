package pt.isel.pdm.chess4android.model

import pt.isel.pdm.chess4android.pieces.*

class PuzzleModel() : GameModel() {

    fun placePieces(pgn: String) {
        beginBoard()
        var armyFlag = true

        val lst: List<String> = pgn.replace("+", "").split(" ")
        var piece: Piece
        for (move: String in lst) {
            //isChecking = '+' in move
            val army = getArmy(armyFlag)
            when (move[0]) {
                'R' -> piece = Rook(army, board, 0, 0, moved = true)

                'B' -> piece = Bishop(army, board, 0, 0)

                'Q' -> piece = Queen(army, board, 0, 0)

                'N' -> piece = Knight(army, board, 0, 0)

                'K' -> piece = King(army, board, 0, 0, moved = true)

                'O' -> {
                    if (move.length == 5) readLeftCastling(armyFlag)
                    else readRightCastling(armyFlag)
                    armyFlag = !armyFlag
                    newArmyToPlay = getArmy(armyFlag)
                    continue
                }
                else -> piece = Pawn(army, board, 0, 0)
            }
            piece.movePGN(move)
            armyFlag = !armyFlag
        }
        newArmyToPlay = getArmy(armyFlag)
    }

    fun placeSolutionOnBoard(): Array<Array<Piece?>> {
        for (solution in solutions) {
            movePiece(solution.first, solution.second)
        }
        return board
    }

    fun getAvailableSolution(col: Int, line: Int): Coord? {
        if (solutions.isEmpty())
            return null

        val sol = solutions[0]
        val prevCoord = sol.first

        return if (prevCoord.col == col && prevCoord.line == line) sol.second
        else null
    }

    fun removeSolutionSelected(pair: Pair<Coord?, Coord?>): Boolean {
        return solutions.remove(pair)
    }

    fun convertSolutions(solution: ArrayList<String>) {
        solutions = arrayListOf()
        var prevCol: Int
        var prevLine: Int
        var newCol: Int
        var newLine: Int
        for (s in solution) {
            prevCol = s[0] - 'a'
            prevLine = 8 - s[1].digitToInt()
            newCol = s[2] - 'a'
            newLine = 8 - s[3].digitToInt()
            solutions.add(Pair(Coord(prevCol, prevLine), Coord(newCol, newLine)))
        }
    }

    private fun readLeftCastling(armyFlag: Boolean) {
        val yCoord = if (armyFlag) 7 else 0
        val army = if (armyFlag) Army.WHITE else Army.BLACK
        board[0][yCoord] = null
        board[3][yCoord] = Rook(army, board, 3, 7, true)
        //update King
        board[4][yCoord] = null
        board[2][yCoord] = King(army, board, 2, 7, true)

    }

    private fun readRightCastling(armyFlag: Boolean) {
        val yCoord = if (armyFlag) 7 else 0
        val army = if (armyFlag) Army.WHITE else Army.BLACK

        board[7][yCoord] = null
        board[5][yCoord] = Rook(army, board, 5, yCoord, true)

        //update King
        board[4][yCoord] = null
        board[6][yCoord] = King(army, board, 6, yCoord, true)

    }
}