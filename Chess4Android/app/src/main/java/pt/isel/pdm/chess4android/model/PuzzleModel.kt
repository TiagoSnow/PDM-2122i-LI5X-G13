package pt.isel.pdm.chess4android.model

import pt.isel.pdm.chess4android.pieces.*

class PuzzleModel() : GameModel() {

    fun placePieces(pgn: String) {
        beginBoard()
        var armyFlag = true
        val lst: List<String> = pgn.split(" ")
        var piece: Piece
        for (move: String in lst) {
            isChecking = '+' in move
            val army = getArmy(armyFlag)
            when (move.replace("+", "")[0]) {
                'R' -> piece = Rook(army, board, 0, 0)

                'B' -> piece = Bishop(army, board, 0, 0)

                'Q' -> piece = Queen(army, board, 0, 0)

                'N' -> piece = Knight(army, board, 0, 0)

                'K' -> piece = King(army, board, 0, 0)

                'O' -> {
                    if (move.length == 5) castlingLeft(armyFlag)
                    else castlingRight(armyFlag)
                    armyFlag = !armyFlag
                    newArmyToPlay = getArmy(armyFlag)
                    return
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
}