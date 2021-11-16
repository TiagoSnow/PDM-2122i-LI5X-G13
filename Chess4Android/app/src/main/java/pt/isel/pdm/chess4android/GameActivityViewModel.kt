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

    var board: Array<Array<Pair<Army, Piece>?>> = Array(COLUMNS) { column ->
        Array(LINES) { null }
    }

    fun getArmy(armyFlag: Boolean): Army {
        return if (armyFlag)
            Army.WHITE
        else
            Army.BLACK
    }

    private fun beginBoard() {
        //colocar as peças no estado inicial
        var armyFlag = true
        var army: Army;
        for (line in 0 until 8 step 7) {
            armyFlag = !armyFlag
            army = getArmy(armyFlag)

            for (column in 0 until 8 step 1) {
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
        for (column in 0 until 8 step 1) {
            board[column][1] = Pair(Army.BLACK, Piece.PAWN)
            board[column][6] = Pair(Army.WHITE, Piece.PAWN)
        }
    }

    val dataOfDay: LiveData<PuzzleInfo> = state.getLiveData(GAME_ACTIVITY_VIEW_STATE)

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

    //  pawn  pawn  pawn
    //  e4  |  c5  |d4   |  cxd4  |Nf3|d6|Nxd4|Nf6|Nc3|a6|Bg5|e6
    private fun checkIfPawnExists(col: Int, line: Int, army: Army): Boolean {
        return (board[col][line] != null && board[col][line]?.first == army)
    }

    private fun movePawnPGN(move: String, army: Army) {
        val col = move[0] - 'a'
        val line = 8 - move[1].digitToInt()
        var startingPoint = 0
        if ((line == 3) && army == Army.BLACK ||
            (line == 4) && army == Army.WHITE
        ) {
            if (checkIfPawnExists(col, 1, army)) startingPoint = 1
            else
                if (checkIfPawnExists(col, 2, army)) startingPoint = 2
                else
                    if (checkIfPawnExists(col, 5, army)) startingPoint = 5
                    else
                        if (checkIfPawnExists(col, 6, army)) startingPoint = 6
        } else
            startingPoint = if (army == Army.WHITE) line + 1 else line - 1

        board[col][startingPoint] = null
        board[col][line] = Pair(army, Piece.PAWN)
    }

    private fun moveRookPGN(move: String, army: Army) {
        when (move.length) {
            3 -> {
                val col = move[1] - 'a'
                val line = 8 - move[2].digitToInt()
                for (line in 0 until 7 step 1) {

                }

            }
        }


        val col = move[1] - 'a'
        val line = 8 - move[1].digitToInt()
        var startingPoint = 0
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

    private fun move(piece: PieceId, newColumn: Int, newLine: Int) {
        //procurar a peça no array

        //apagar a peça que vai ser comida

        //colocar a nova peça na nova posicao

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