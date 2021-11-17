package pt.isel.pdm.ches

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import pt.isel.pdm.chess4android.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val GAME_ACTIVITY_VIEW_STATE = "GameActivity.ViewState"
private const val COLUMNS = 8
private const val LINES = 8
private const val MIN_BOARD_VAL = 0
private const val MAX_BOARD_VAL = 7

class GameActivityViewModel(
    application: Application,
    private val state: SavedStateHandle
) :
    AndroidViewModel(application) {
    fun getPuzzleOfDay() {
        this.getApplication<PuzzleOfDayApplication>()
            .puzzleOfDayService
            .getPuzzle()
            .enqueue(object : Callback<PuzzleInfo> {
                override fun onResponse(call: Call<PuzzleInfo>, response: Response<PuzzleInfo>) {
                    val puzzle = response.body()
                    if (puzzle != null && response.isSuccessful)
                        state.set(GAME_ACTIVITY_VIEW_STATE, puzzle)
                }

                override fun onFailure(call: Call<PuzzleInfo>, t: Throwable) {
                    Log.e("APP", "onFailure", t)
                }
            })
    }

    val dataOfDay: LiveData<PuzzleInfo> = state.getLiveData(GAME_ACTIVITY_VIEW_STATE)


    var board: Array<Array<Pair<Army, Piece>?>> = Array(COLUMNS) {
        Array(LINES) { null }
    }

    private fun getArmy(armyFlag: Boolean): Army {
        return if (armyFlag)
            Army.WHITE
        else
            Army.BLACK
    }

    private fun fillHalfBoard(line: Int, army: Army) {
        for (column in 0..8) {
            when (column) {
                0, 7 -> {
                    board[column][line] = Pair(army, Piece.ROOK)

                }
                1, 6 -> {
                    board[column][line] = Pair(army, Piece.KNIGHT)

                }
                2, 5 -> {
                    board[column][line] = Pair(army, Piece.BISHOP)

                }
                3 -> {
                    board[column][line] = Pair(army, Piece.QUEEN)

                }
                4 -> {
                    board[column][line] = Pair(army, Piece.KING)

                }
            }
        }
    }

    private fun beginBoard() {
        //colocar as peças no estado inicial

        fillHalfBoard(0, getArmy(false))
        fillHalfBoard(7, getArmy(true))

        for (column in 0..MAX_BOARD_VAL) {
            board[column][1] = Pair(Army.BLACK, Piece.PAWN)
            board[column][6] = Pair(Army.WHITE, Piece.PAWN)
        }
    }

    private fun putPiece(col: Int, line: Int, army: Army, piece: Piece) {
        board[col][line] = Pair(army, piece)
    }



    private fun moveKnightPNG(move: String, army: Army) {
        val col: Int
        val line: Int
        if (move.length == 3) {
            col = move[1] - 'a'
            line = 8 - move[2].digitToInt()
        } else {
            col = move[2] - 'a'
            line = 8 - move[3].digitToInt()
        }
        val startPositions = searchKnight(col, line, army)

        board[startPositions.first][startPositions.second] = null
        board[col][line] = Pair(army, Piece.KNIGHT)
    }

    /*private fun zz(x:Int, y:Int, xOffset:Int, yOffset:Int,army: Army,piece: Piece): Pair<Int, Int>? {
        if(x + xOffset  in 0..7){
            if(y+yOffset in 0..7 && checkIfPieceExists(x+xOffset,y+yOffset,army,Piece.KNIGHT)) return Pair(x+xOffset,y+yOffset)
            if(y-yOffset in 0..7 && checkIfPieceExists(x,y-yOffset,army,Piece.KNIGHT)) return Pair(x+xOffset,y-yOffset)
        }
        return null
    }
*/
    private fun searchKnight(col: Int, line: Int, army: Army): Pair<Int, Int> {
        if (col + 2 in 0..7) {
            if (line + 1 in 0..7 && checkIfPieceExists(
                    col + 2,
                    line + 1,
                    army,
                    Piece.KNIGHT
                )
            ) return Pair(col + 2, line + 1)
            if (line - 1 in 0..7 && checkIfPieceExists(
                    col + 2,
                    line - 1,
                    army,
                    Piece.KNIGHT
                )
            ) return Pair(col + 2, line - 1)
        }
        if (col - 2 in 0..7) {
            if (line + 1 in 0..7 && checkIfPieceExists(
                    col - 2,
                    line + 1,
                    army,
                    Piece.KNIGHT
                )
            ) return Pair(col - 2, line + 1)
            if (line - 1 in 0..7 && checkIfPieceExists(
                    col - 2,
                    line - 1,
                    army,
                    Piece.KNIGHT
                )
            ) return Pair(col - 2, line - 1)
        }
        if (line + 2 in 0..7) {
            if (col + 1 in 0..7 && checkIfPieceExists(
                    col + 1,
                    line + 2,
                    army,
                    Piece.KNIGHT
                )
            ) return Pair(col + 1, line + 2)
            if (col - 1 >= 0 && checkIfPieceExists(
                    col - 1,
                    line + 2,
                    army,
                    Piece.KNIGHT
                )
            ) return Pair(col - 1, line + 2)
        }
        if (line - 2 in 0..7) {
            if (col + 1 in 0..7 && checkIfPieceExists(
                    col + 1,
                    line - 2,
                    army,
                    Piece.KNIGHT
                )
            ) return Pair(col + 1, line - 2)
            if (col - 1 in 0..7 && checkIfPieceExists(
                    col - 1,
                    line - 2,
                    army,
                    Piece.KNIGHT
                )
            ) return Pair(col - 1, line - 2)
        }
        return Pair(-1, -1)
    }

    private fun searchBishop(tileStart: Int, army: Army): Pair<Int, Int> {
        var startColPosition = 0
        var startLinePosition = 0
        var initLineAux = tileStart - 1
        for (c in 0..MAX_BOARD_VAL) {
            initLineAux = if (initLineAux == 0) 1 else 0
            for (l in initLineAux until 8 step 2)
                if (board[c][l]?.second == Piece.BISHOP && board[c][l]?.first == army) {
                    startColPosition = c
                    startLinePosition = l
                    break
                }
        }
        return Pair(startColPosition, startLinePosition)
    }

    private fun moveBishopPGN(move: String, army: Army) {
        val col: Int
        val line: Int
        if (move.length == 3) {
            col = move[1] - 'a'
            line = 8 - move[2].digitToInt()
        } else {
            col = move[2] - 'a'
            line = 8 - move[3].digitToInt()
        }
        val startPositions = searchBishop((col + line) % 2, army)//tile => 0 = white, 1 = black

        board[startPositions.first][startPositions.second] = null
        board[col][line] = Pair(army, Piece.BISHOP)
    }


    private fun movePawnPGN(move: String, army: Army) {
        val col: Int
        val line: Int
        var startingPoint = 0
        if (move.length == 4) {
            col = move[2] - 'a'
            line = 8 - move[3].digitToInt()
            val startColumn = move[0] - 'a'
            for (l in 0..7) {
                if (checkIfPieceExists(startColumn, l, army, Piece.PAWN)) {
                    startingPoint = l
                    break
                }
            }
        } else {
            col = move[0] - 'a'
            line = 8 - move[1].digitToInt()
            if ((line == 3) && army == Army.BLACK ||
                (line == 4) && army == Army.WHITE
            ) when {
                //mais clean?
                checkIfPieceExists(col, 1, army, Piece.PAWN) -> startingPoint = 1
                checkIfPieceExists(col, 2, army, Piece.PAWN) -> startingPoint = 2
                checkIfPieceExists(col, 5, army, Piece.PAWN) -> startingPoint = 5
                checkIfPieceExists(col, 6, army, Piece.PAWN) -> startingPoint = 6
            } else
                startingPoint = if (army == Army.WHITE) line + 1 else line - 1
        }
        board[col][startingPoint] = null
        putPiece(col, line, army, Piece.PAWN)
    }

    private fun searchRookInSameCol(
        colFrom: Int,
        colDest: Int,
        lineDest: Int,
        army: Army,
    ): Int {
        return if (colFrom == colDest) {
            var line = 0
            while (line < MAX_BOARD_VAL) {
                if (checkIfPieceExists(colFrom, line, army, Piece.ROOK))
                    break
                line++
            }
            line
        } else lineDest
    }

    private fun searchRook(colDest: Int, lineDest: Int, army: Army): Pair<Int, Int> {
        var colFrom = 0
        var lineFrom = 0
        for (i in 1..7) {
            if (lineDest + i <= MAX_BOARD_VAL && checkIfPieceExists(
                    colDest,
                    lineDest + i,
                    army,
                    Piece.ROOK
                )
            ) {
                colFrom = colDest
                lineFrom = lineDest + i
                break
            }
            //left search
            if (lineDest - i >= MIN_BOARD_VAL && checkIfPieceExists(
                    colDest,
                    lineDest - i,
                    army,
                    Piece.ROOK
                )
            ) {
                colFrom = colDest
                lineFrom = lineDest - i
                break
            }
            //searches for rook in previous position vertically
            //right search
            if (colDest + i <= MAX_BOARD_VAL && checkIfPieceExists(
                    colDest + i,
                    lineDest,
                    army,
                    Piece.ROOK
                )
            ) {
                colFrom = colDest + i
                lineFrom = lineDest
                break
            }
            //left search
            if (colDest - i >= MIN_BOARD_VAL && checkIfPieceExists(
                    colDest - i,
                    lineDest,
                    army,
                    Piece.ROOK
                )
            ) {
                colFrom = colDest - i
                lineFrom = lineDest
                break
            }
        }
        return Pair(colFrom, lineFrom)
    }

    private fun moveRookPGN(move: String, army: Army) {
        var colDest = 0
        var lineDest = 0
        var colFrom = 0
        var lineFrom = 0
        when (move.length) {
            3 -> {
                //Destination values
                colDest = move[1] - 'a'
                lineDest = 8 - move[2].digitToInt()
                val coords = searchRook(colDest, lineDest, army)
                colFrom = coords.first
                lineFrom = coords.second
            }
            4 -> {
                colDest = move[2] - 'a'
                lineDest = 8 - move[3].digitToInt()
                // If capture
                if (move[1] == 'x') {
                    //Rxc7
                    val coords = searchRook(colDest, lineDest, army)
                    colFrom = coords.first
                    lineFrom = coords.second
                } else {
                    if (move[1] in '0'..'9') {      //R3c4
                        lineFrom = 8 - move[1].digitToInt()
                        colFrom = colDest
                    } else {                        //Rhc3
                        colFrom = move[1] - 'a'
                        //specific case where the column stays the same in vertical move (ex:Raa3)
                        lineFrom = searchRookInSameCol(colFrom, colDest, lineDest, army)
                    }
                }
            }
            5 -> { //R4xc5 || Rdxd5
                colDest = move[3] - 'a'
                lineDest = 8 - move[4].digitToInt()
                if (move[1] in '0'..'9') {
                    colFrom = colDest
                    lineFrom = 8 - move[1].digitToInt()
                } else {
                    colFrom = move[1] - 'a'
                    lineFrom = searchRookInSameCol(colFrom, colDest, lineDest, army)
                }
            }
        }
        board[colFrom][lineFrom] = null
        putPiece(colDest, lineDest, army, Piece.ROOK)
    }

    private fun moveKingPGN(move: String, army: Army) {
        val col: Int
        val line: Int

        if (move.length == 4) {
            col = move[2] - 'a'
            line = 8 - move[3].digitToInt()
        } else {
            col = move[1] - 'a'
            line = 8 - move[2].digitToInt()
        }
        val startPositions = searchKing(col, line, army)

        board[startPositions.first][startPositions.second] = null
        putPiece(col, line, army, Piece.KING)
    }

    private fun castlingLeft() {
        //update Rook
        board[0][0] = null
        board[3][0] = Pair(Army.BLACK, Piece.ROOK)

        //update King
        board[4][0] = null
        board[2][0] = Pair(Army.BLACK, Piece.KING)
    }

    private fun castlingRight() {
        //update Rook
        board[7][7] = null
        board[5][7] = Pair(Army.WHITE, Piece.ROOK)

        //update King
        board[4][7] = null
        board[6][7] = Pair(Army.WHITE, Piece.KING)
    }

    private fun searchKing(col: Int, line: Int, army: Army): Pair<Int, Int> {
        val piece = Piece.KING

        if (col - 1 in 0..7 && line - 1 in 0..7)
            if (checkIfPieceExists(col - 1, line - 1, army, piece)) return Pair(
                col - 1,
                line - 1
            )      //diagonal up left

        if ((col in 0..7) && line - 1 in 0..7)
            if (checkIfPieceExists(col, line - 1, army, piece)) return Pair(
                col,
                line - 1
            )              //up

        if (col + 1 in 0..7 && line - 1 in 0..7)
            if (checkIfPieceExists(col + 1, line - 1, army, piece)) return Pair(
                col + 1,
                line - 1
            )      //diagonal up right

        if (col - 1 in 0..7 && line in 0..7)
            if (checkIfPieceExists(col - 1, line, army, piece)) return Pair(
                col - 1,
                line
            )              //left

        if (col + 1 in 0..7 && line in 0..7)
            if (checkIfPieceExists(col + 1, line, army, piece)) return Pair(
                col + 1,
                line
            )              //right

        if (col - 1 in 0..7 && line + 1 in 0..7)
            if (checkIfPieceExists(col - 1, line + 1, army, piece)) return Pair(
                col - 1,
                line + 1
            )      //diagonal down left

        if ((col in 0..7) && line + 1 in 0..7)
            if (checkIfPieceExists(col, line + 1, army, piece)) return Pair(
                col,
                line + 1
            )             //down

        if (col + 1 in 0..7 && line + 1 in 0..7)
            if (checkIfPieceExists(col + 1, line + 1, army, piece)) return Pair(
                col + 1,
                line + 1
            )     //diagonal down right

        return Pair(-1, -1);
    }

    fun placePieces(pgn: String) {
        beginBoard()
        var armyFlag = true
        val lst: List<String> = pgn.split(" ")
        for (move: String in lst) {
            val army = getArmy(armyFlag)
            when (move[0]) {
                'R' -> moveRookPGN(move, army)
                'B' -> moveBishopPGN(move, army)
                'Q' -> {
                }
                'N' -> {
                }
                'K' -> {
                }
                'O' -> {
                    if (move.length == 5)
                        castlingLeft()
                    else
                        castlingRight()
                }
                else -> movePawnPGN(move, army)
            }
            armyFlag = !armyFlag
        }
    }

    private fun getPiece(char: Char): Piece {
        return when (char) {
            'B' -> Piece.BISHOP
            'N' -> Piece.KNIGHT
            'R' -> Piece.ROOK
            'K' -> Piece.KING
            else -> {
                Piece.QUEEN
            }
        }
    }
}