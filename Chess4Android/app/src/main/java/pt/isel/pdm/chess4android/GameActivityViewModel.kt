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

    private fun checkIfPieceExists(col: Int, line: Int, army: Army, piece: Piece): Boolean {
        return (board[col][line] != null
                && board[col][line]?.first == army
                && board[col][line]?.second == piece)
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

        board[startPositions[0]][startPositions[1]] = null
        board[col][line] = Pair(army, Piece.BISHOP)
    }

    private fun searchBishop(tileStart: Int, army: Army): Array<Int> {
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
        return arrayOf(startColPosition, startLinePosition)
    }

    private fun movePawnPGN(move: String, army: Army) {
        val col: Int
        val line: Int
        var startingPoint = 0
        if (move.length == 3) {
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
                //searches for rook in previous position horizontally
                //right search
                val coords = searchRook(colDest, lineDest, army)
                colFrom = coords.first
                lineFrom = coords.second

            }
            4 -> {
                // If capture
                if (move[1] == 'x') {
                    //Rxc7
                    colDest = move[2] - 'a'
                    lineDest = move[3].digitToInt()

                } else {
                    //R3c4
                    if (move[1] in '0'..'9') {
                        lineFrom = 8 - move[1].digitToInt()
                        lineDest = 8 - move[3].digitToInt()
                        colDest = move[2] - 'a'
                        colFrom = colDest
                        //Rhc3
                    } else {
                        colFrom = move[1] - 'a'
                        colDest = move[2] - 'a'
                        lineDest = 8 - move[3].digitToInt()
                        //specific case where the column stays the same in vertical move (ex:Raa3)
                        lineFrom =
                            if (colFrom == colDest) {
                                var line = 0
                                while (line < MAX_BOARD_VAL) {
                                    if (checkIfPieceExists(colFrom, line, army, Piece.ROOK))
                                        break
                                    line++
                                }
                                line
                            } else lineDest
                    }
                }
            }
        }
        board[colFrom][lineFrom] = null
        putPiece(colDest, lineDest, army, Piece.ROOK)
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