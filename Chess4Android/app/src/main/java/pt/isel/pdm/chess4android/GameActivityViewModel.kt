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


    var board: Array<Array<Pair<Army, Piece>?>> = Array(COLUMNS) { column ->
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

        for (column in 0..7) {
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
        for (c in 0 until 8 step 1) {
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
        val col = move[0] - 'a'
        val line = 8 - move[1].digitToInt()
        var startingPoint = 0
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

        board[col][startingPoint] = null
        putPiece(col, line, army, Piece.PAWN)
    }

    private fun moveRookPGN(move: String, army: Army) {
        val colDest: Int
        val lineDest: Int
        when (move.length) {
            3 -> {
                //Destination values
                colDest = move[1] - 'a'
                lineDest = 8 - move[2].digitToInt()

                var foundRook = false
                //if destination is on the right or left side of the board.
                //In case more than 1 rook is found in a line
                if (colDest > 3) {
                    for (line in 7 downTo 0) {
                        if (checkIfPieceExists(colDest, line, army, Piece.ROOK)) {
                            foundRook = true;
                            board[colDest][line] = null
                            break
                        }
                    }
                } else {
                    for (line in 0..8) {
                        if (checkIfPieceExists(colDest, line, army, Piece.ROOK)) {
                            foundRook = true;
                            board[colDest][line] = null
                            break
                        }
                    }
                }

                //if rook not found vertically, search horizontally
                if (!foundRook) {
                    if (lineDest > 3) {
                        for (col in 7 downTo 0) {
                            if (checkIfPieceExists(col, lineDest, army, Piece.ROOK)) {
                                foundRook = true;
                                board[col][lineDest] = null
                                break
                            }
                        }
                    } else {
                        for (col in 0..8) {
                            if (checkIfPieceExists(col, lineDest, army, Piece.ROOK)) {
                                foundRook = true;
                                board[col][lineDest] = null
                                break
                            }
                        }
                    }
                }
                putPiece(colDest, lineDest, army, Piece.ROOK)
            }
            4 -> {
                val colFrom: Int
                val lineFrom: Int
                // If capture
                if (move[1] == 'x') {

                } else {
                    if (move[1] in '0'..'9') {
                        lineFrom = move[1].digitToInt()
                        lineDest = move[3].digitToInt()
                        colDest = move[2] - 'a'
                        colFrom = colDest
                    } else {
                        colFrom = move[1] - 'a'
                        //specific case where the column stays the same in vertical move
                        if (colFrom == move[2] - 'a') {

                        } else colDest = move[2] - 'a'

                        lineFrom = move[3].digitToInt()
                        lineDest = lineFrom
                    }
                    putPiece(colDest, lineDest, army, Piece.ROOK)
                    board[colFrom][lineFrom] = null
                }
            }
        }
    }

    fun placePieces(pgn: String) {
        beginBoard()
        var armyFlag = true
        val lst: List<String> = pgn.split(" ")
        for (move: String in lst) {
            val army = getArmy(armyFlag)
            when (move[0]) {
                'R' -> moveRookPGN(move, army)
                'B' -> {
                }
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