package pt.isel.pdm.chess4android.model

import pt.isel.pdm.chess4android.pieces.*

class PuzzleModel () : GameModel(){

    fun placePieces(pgn: String) {
        beginBoard()
        var armyFlag = true
        val lst: List<String> = pgn.split(" ")
        for (move: String in lst) {
            isChecking = '+' in move
            val army = getArmy(armyFlag)
            when (move.replace("+", "")[0]) {
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
                    val knight = Knight(army, board, -1, -1)
                    knight.movePGN(move)
                    updateCheckingPiece(knight)
                    newArmyToPlay = getArmy(!armyFlag)
                    if (stopPieceFromMoving(knight)) {
                        //  knight.movePGN(move)
                    }
                    knight.updateBoard()
                    newArmyToPlay = getArmy(!armyFlag)
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