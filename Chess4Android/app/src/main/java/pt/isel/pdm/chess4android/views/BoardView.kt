package pt.isel.pdm.chess4android.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.GridLayout
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import pt.isel.pdm.chess4android.*

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

    private fun createImageEntry(army: Army, piece: Piece, imageId: Int) =
        Pair(Pair(army, piece), VectorDrawableCompat.create(ctx.resources, imageId, null))

    private val piecesImages = mapOf(
        createImageEntry(Army.WHITE, Piece.PAWN, R.drawable.ic_white_pawn),
        createImageEntry(Army.WHITE, Piece.KNIGHT, R.drawable.ic_white_knight),
        createImageEntry(Army.WHITE, Piece.BISHOP, R.drawable.ic_white_bishop),
        createImageEntry(Army.WHITE, Piece.ROOK, R.drawable.ic_white_rook),
        createImageEntry(Army.WHITE, Piece.QUEEN, R.drawable.ic_white_queen),
        createImageEntry(Army.WHITE, Piece.KING, R.drawable.ic_white_king),
        createImageEntry(Army.BLACK, Piece.PAWN, R.drawable.ic_black_pawn),
        createImageEntry(Army.BLACK, Piece.KNIGHT, R.drawable.ic_black_knight),
        createImageEntry(Army.BLACK, Piece.BISHOP, R.drawable.ic_black_bishop),
        createImageEntry(Army.BLACK, Piece.ROOK, R.drawable.ic_black_rook),
        createImageEntry(Army.BLACK, Piece.QUEEN, R.drawable.ic_black_queen),
        createImageEntry(Army.BLACK, Piece.KING, R.drawable.ic_black_king),
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
                piecesImages,
                Pair(Army.WHITE,Piece.KNIGHT)
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

    fun updateView(board: Array<Array<Pair<Army, Piece>?>>) {
        for (column in 0 until 8 step 1){
            for (line in 0 until 8 step 1) {
                tiles[column][line]?.piece = board[column][line]
            }
        }



       // val tile = Tile(ctx, color, 8, piecesImages, Pair(color, pieceId.piece))

        //removeView(tiles[0][0])
        //addView(tile)

        //tiles[0][0]?.piece = Pair(Army.WHITE,Piece.BISHOP)
    }

    companion object {
        private const val COLUMNS = 8
        private const val LINES = 8
    }


}