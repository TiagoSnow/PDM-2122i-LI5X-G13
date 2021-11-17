package pt.isel.pdm.chess4android.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.GridLayout
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import pt.isel.pdm.chess4android.*
import pt.isel.pdm.chess4android.pieces.Piece

/**
 * Custom view that implements a chess board.
 */
@SuppressLint("ClickableViewAccessibility")
class BoardView(private val ctx: Context, attrs: AttributeSet?) : GridLayout(ctx, attrs) {
    // var model = GameActivityViewModel.
    private val side = 8

    private var tiles: Array<Array<Tile?>> = Array(COLUMNS) {
        Array(LINES) { null }
    }

    private val brush = Paint().apply {
        ctx.resources.getColor(R.color.chess_board_black, null)
        style = Paint.Style.STROKE
        strokeWidth = 10F
    }

    private fun createImageEntry(army: Army, pieces: Pieces, imageId: Int) =
        Pair(Pair(army, pieces), VectorDrawableCompat.create(ctx.resources, imageId, null))

    private val piecesImages = mapOf(
        createImageEntry(Army.WHITE, Pieces.PAWN, R.drawable.ic_white_pawn),
        createImageEntry(Army.WHITE, Pieces.KNIGHT, R.drawable.ic_white_knight),
        createImageEntry(Army.WHITE, Pieces.BISHOP, R.drawable.ic_white_bishop),
        createImageEntry(Army.WHITE, Pieces.ROOK, R.drawable.ic_white_rook),
        createImageEntry(Army.WHITE, Pieces.QUEEN, R.drawable.ic_white_queen),
        createImageEntry(Army.WHITE, Pieces.KING, R.drawable.ic_white_king),
        createImageEntry(Army.BLACK, Pieces.PAWN, R.drawable.ic_black_pawn),
        createImageEntry(Army.BLACK, Pieces.KNIGHT, R.drawable.ic_black_knight),
        createImageEntry(Army.BLACK, Pieces.BISHOP, R.drawable.ic_black_bishop),
        createImageEntry(Army.BLACK, Pieces.ROOK, R.drawable.ic_black_rook),
        createImageEntry(Army.BLACK, Pieces.QUEEN, R.drawable.ic_black_queen),
        createImageEntry(Army.BLACK, Pieces.KING, R.drawable.ic_black_king),
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
            //tile.setOnClickListener { onTileClickedListener?.invoke(tile, row, column) }
            addView(tile)
            tiles[column][row] = tile
        }
    }
    //var onTileClickedListener: TileTouchListener? = null

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        canvas.drawLine(0f, 0f, width.toFloat(), 0f, brush)
        canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), brush)
        canvas.drawLine(0f, 0f, 0f, height.toFloat(), brush)
        canvas.drawLine(width.toFloat(), 0f, width.toFloat(), height.toFloat(), brush)
    }

    fun updateView(board: Array<Array<Piece?>>) {
        for (column in 0..7) {
            for (line in 0..7) {
                val piece = board[column][line]
                if(piece != null)
                    tiles[column][line]?.pieces = Pair(piece.army, piece.piece)
                else
                    tiles[column][line]?.pieces = null
            }
        }
    }

    companion object {
        private const val COLUMNS = 8
        private const val LINES = 8
    }


}