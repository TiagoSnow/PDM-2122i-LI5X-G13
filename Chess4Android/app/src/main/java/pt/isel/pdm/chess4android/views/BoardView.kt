package pt.isel.pdm.chess4android.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.widget.GridLayout
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import pt.isel.pdm.chess4android.*
import pt.isel.pdm.chess4android.pieces.Coord
import pt.isel.pdm.chess4android.pieces.Piece

/**
 * Custom view that implements a chess board.
 */

typealias TileTouchListener = (tile: Tile, row: Int, column: Int) -> Unit

@SuppressLint("ClickableViewAccessibility")
class BoardView(private val ctx: Context, attrs: AttributeSet?) : GridLayout(ctx, attrs) {
    // var model = GameActivityViewModel.

    private val side = 8

    private var tiles: Array<Array<Tile?>> = Array(COLUMNS) {
        Array(LINES) { null }
    }

    private lateinit var board: Array<Array<Piece?>>

    private val brush = Paint().apply {
        ctx.resources.getColor(R.color.chess_board_black, null)
        style = Paint.Style.STROKE
        strokeWidth = 10F
    }

    private var circle_img =
        VectorDrawableCompat.create(ctx.resources, R.drawable.circle, null) // ver onde se utilizar

    private fun createImageEntry(army: Army, piecesType: PiecesType, imageId: Int) =
        Pair(Pair(army, piecesType), VectorDrawableCompat.create(ctx.resources, imageId, null))

    private val piecesImages = mapOf(
        createImageEntry(Army.WHITE, PiecesType.PAWN, R.drawable.ic_white_pawn),
        createImageEntry(Army.WHITE, PiecesType.KNIGHT, R.drawable.ic_white_knight),
        createImageEntry(Army.WHITE, PiecesType.BISHOP, R.drawable.ic_white_bishop),
        createImageEntry(Army.WHITE, PiecesType.ROOK, R.drawable.ic_white_rook),
        createImageEntry(Army.WHITE, PiecesType.QUEEN, R.drawable.ic_white_queen),
        createImageEntry(Army.WHITE, PiecesType.KING, R.drawable.ic_white_king),
        createImageEntry(Army.BLACK, PiecesType.PAWN, R.drawable.ic_black_pawn),
        createImageEntry(Army.BLACK, PiecesType.KNIGHT, R.drawable.ic_black_knight),
        createImageEntry(Army.BLACK, PiecesType.BISHOP, R.drawable.ic_black_bishop),
        createImageEntry(Army.BLACK, PiecesType.ROOK, R.drawable.ic_black_rook),
        createImageEntry(Army.BLACK, PiecesType.QUEEN, R.drawable.ic_black_queen),
        createImageEntry(Army.BLACK, PiecesType.KING, R.drawable.ic_black_king),
    )

    init {
        rowCount = side
        columnCount = side
        repeat(side * side) {
            val row = it / side
            val column = it % side
            val tile = Tile(
                ctx,
                if ((row + column) % 2 == 0) Army.WHITE else Army.BLACK,
                side,
                piecesImages
            )

            tile.setOnClickListener {
                onTileClickedListener?.invoke(tile, row, column)

                val options = getAvailableOptions(row, column)

                if(tile.isAlreadySelected) {
                    for(coordinate in options) {
                        val c = coordinate?.first?.col
                        val l = coordinate?.first?.line

                        setOriginalColor(l!!, c!!, tiles[c][l]!!)
                    }
                    setOriginalColor(row, column, tile)
                    tile.isAlreadySelected = false
                }
                else {
                    changeBackgroundColor(tile, Color.GREEN)

                    //pintar todas as opções
                    if(!options.isEmpty()) {
                        for(coordinate in options) {
                            val c = coordinate?.first?.col
                            val l = coordinate?.first?.line

                            changeBackgroundColor(tiles[c!!][l!!]!!, Color.RED)
                        }
                    }

                    tile.isAlreadySelected = true
                }


                Log.v("App", row.toString() + " : "+ column)
            }


            addView(tile)
            tiles[column][row] = tile
        }
    }

    private fun setOriginalColor(row: Int, column: Int, tile: Tile) {
        when ((row + column) % 2) {
            0 -> changeBackgroundColor(tile, ctx.resources.getColor(R.color.chess_board_white, null))

            1 -> changeBackgroundColor(tile, ctx.resources.getColor(R.color.chess_board_black, null))
        }
    }

    private fun getAvailableOptions(row: Int, column: Int): MutableList<Pair<Coord, Boolean>?> {
        val piece = board[column][row]
        if(piece != null)
            return piece.searchRoute()

        return mutableListOf()
    }

    fun changeBackgroundColor(tile: Tile, color: Int) {
        tile.setBackgroundColor(color)
        tile.brush.color = color
    }

    var onTileClickedListener: TileTouchListener? = null



    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        canvas.drawLine(0f, 0f, width.toFloat(), 0f, brush)
        canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), brush)
        canvas.drawLine(0f, 0f, 0f, height.toFloat(), brush)
        canvas.drawLine(width.toFloat(), 0f, width.toFloat(), height.toFloat(), brush)
    }

    fun updateView(board: Array<Array<Piece?>>) {
        this.board = board
        for (column in 0..7) {
            for (line in 0..7) {
                val piece = board[column][line]
                if(piece != null)
                    tiles[column][line]?.piecesType = Pair(piece.army, piece.piece)
                else
                    tiles[column][line]?.piecesType = null
            }
        }
    }

    companion object {
        private const val COLUMNS = 8
        private const val LINES = 8
    }

}