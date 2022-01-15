package pt.isel.pdm.tictactoe.game.model

import pt.isel.pdm.chess4android.model.Army
import pt.isel.pdm.chess4android.pieces.*

/**
 * Represents a Tic-Tac-Toe board. Instances are immutable.
 *
 * @property turn   The next player to move, or null if the game has already ended
 * @property board  The board tiles
 */

const val BOARD_SIZE = 8

data class Board(
    val turn: Army? = Army.firstToMove,
    var board: Array<Array<Piece?>> = Array(BOARD_SIZE) { Array<Piece?>(BOARD_SIZE) { null } }
) {

    /**
     * Overloads the indexing operator
     */
    operator fun get(at: Coord): Piece? = getPiece(at)

    /**
     * Gets the move at the given coordinates.
     *
     * @param at    the move's coordinates
     * @return the [Player] instance that made the move, or null if the position is empty
     */
    fun getPiece(at: Coord): Piece? = board[at.col][at.line]

    /**
     * Makes a move at the given coordinates and returns the new board instance.
     *
     * @param new    the board's coordinate
     * @throws IllegalArgumentException if the position is already occupied
     * @throws IllegalStateException if the game has already ended
     * @return the new board instance
     */
    fun makeMove(new: Coord, prev: Coord): Array<Array<Piece?>> {
        require(board[new.col][new.line] == null)
        checkNotNull(turn)

        val movedPiece = get(prev)

        //Atualizar coords da peça movida
        movedPiece?.col = new.col
        movedPiece?.line = new.line

        //Atualizar model
        board[new.col][new.line] = movedPiece
        board[prev.col][prev.line] = null
        return board

    }

    /**
     * Converts this instance to a list of moves.
     */
    fun toArray(): Array<Array<Piece?>> = board
}

/**
 * Extension function that checks whether this board's position [at] is free or not
 *
 * @param at    the board's coordinate
 * @return true if the board position is free, false otherwise
 */
fun Board.isFree(at: Coord): Boolean = board[at.col][at.line] == null

/**
 * Extension function that checks whether this board's position [at] is free or not
 *
 * @param at    the board's coordinate
 * @return true if the board position is occupied, false otherwise
 */
fun Board.isNotFree(at: Coord): Boolean = !isFree(at)



