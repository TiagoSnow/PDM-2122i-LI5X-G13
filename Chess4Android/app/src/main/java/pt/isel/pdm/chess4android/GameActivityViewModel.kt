package pt.isel.pdm.chess4android

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import pt.isel.pdm.chess4android.views.BoardView
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

     var board: Array<Array<PieceId?>> = Array(COLUMNS) { column ->
        Array(LINES) { null }
    }

    fun beginBoard() {
        //colocar as peças no estado inicial

        var armyFlag = true
        for (line in 0 until 8 step 7) {
            armyFlag = !armyFlag
            for (column in 0 until 8 step 1) {
                when (column) {
                    0, 7 -> {
                        board[column][line] = PieceId(armyFlag, Piece.ROOK)

                    }
                    1, 6 -> {
                        board[column][line] = PieceId(armyFlag, Piece.KNIGHT)

                    }
                    2, 5 -> {
                        board[column][line] = PieceId(armyFlag, Piece.BISHOP)

                    }
                    3 -> {
                        board[column][line] = PieceId(armyFlag, Piece.QUEEN)

                    }
                    4 -> {
                        board[column][line] = PieceId(armyFlag, Piece.KING)

                    }
                }
            }
        }

        for (column in 0 until 8 step 1) {
            board[column][1] = PieceId(false, Piece.PAWN)//white
            board[column][6] = PieceId(true, Piece.PAWN)//black
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

    fun setBoard(data: PuzzleInfo, boardView: BoardView) {
        Log.v("APP", data.game.toString())
        Log.v("APP", data.puzzle.solution.toString())

        beginBoard()

        for (line in 0 until 8 step 1) {
            for (column in 0 until 8 step 1) {
                val value = board[column][line]
                if(value != null)
                    boardView.updateView(value)
            }
        }

    }

    fun placePieces(pgn: String) {
        var armyFlag = false
        val lst: List<String> = pgn.split(" ")
        for (p: String in lst) {
            when (p.length) {
                2 -> /*pawn*/ {
                    board[p[0] - 'A'][Integer.parseInt(p[1].toString())] =
                        PieceId(armyFlag, Piece.PAWN)
                    armyFlag = !armyFlag
                }
                3 -> {//Qd2
                    /*create other piece*/
                    var piece = getPiece(p[0])
                    board[p[1] - 'A'][Integer.parseInt(p[2].toString())] =
                        PieceId(armyFlag, piece)
                    armyFlag = !armyFlag
                    //if (p == "O-O")
                        //move()
                    /*Kingside Castle*/
                }
                else -> {
                    if (p == "O-O-O")
                    /*Queenside castle*/
                    /*Move*/
                        move(
                            PieceId(armyFlag, getPiece(p[0])),
                            p[2] - 'A',
                            Integer.parseInt(p[3].toString())
                        )
                    armyFlag = !armyFlag
                }
            }

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