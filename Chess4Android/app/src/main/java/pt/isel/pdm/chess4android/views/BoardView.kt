package pt.isel.pdm.chess4android.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.widget.GridLayout
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import pt.isel.pdm.chess4android.*
import pt.isel.pdm.chess4android.model.Army
import pt.isel.pdm.chess4android.model.GameModel
import pt.isel.pdm.chess4android.model.PiecesType
import pt.isel.pdm.chess4android.pieces.Coord
import pt.isel.pdm.chess4android.pieces.Piece

/**
 * Custom view that implements a chess board.
 */

typealias TileTouchListener = (tile: Tile, row: Int, column: Int) -> Unit

@SuppressLint("ClickableViewAccessibility")
class BoardView(private val ctx: Context, attrs: AttributeSet?) : GridLayout(ctx, attrs) {

    private lateinit var game: GameModel
    private val side = 8
    private var tiles: Array<Array<Tile?>> = Array(COLUMNS) {
        Array(LINES) { null }
    }
    private var options: MutableList<Coord?> = mutableListOf()
    private var prevCoord: Coord? = null
    private var newArmyToPlay: Army = Army.WHITE
    private var checkPair: Pair<Coord, Coord>? = null
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

    var listener: BoardClickListener?

    init {
        listener = null
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

                //Apaga as selected independentemente do sitio do próximo clique

                setOriginalColorToAllOptions()

                if (tile.isAlreadySelected) {
                    setOriginalColor(row, column, tile)
                    resetOptions()
                    tile.isAlreadySelected = false
                } else {
                    if (isTileAnOption(column, row)) {
                        listener?.onMovement(prevCoord, Coord(column, row))
                        options.clear()
                        setPreviousColor()
                    } else {
                        if (prevCoord != null) {
                            setPreviousColor()

                        } else {
                            prevCoord = Coord(column, row)
                        }
                        //Colocar cor na tile atual
                        changeBackgroundColor(tile, Color.DKGRAY)
                        tile.isAlreadySelected = true

                        prevCoord?.col = column
                        prevCoord?.line = row

                        listener?.onTileClicked(column, row)
                    }
                }

                Log.v("App", "$row : $column")
            }
            addView(tile)
            tiles[column][row] = tile
        }
    }

    private fun setPreviousColor() {
        //Colocar cor original na tile anteriormente selecionada
        setOriginalColor(
            prevCoord!!.line,
            prevCoord!!.col,
            tiles[prevCoord!!.col][prevCoord!!.line]!!
        )
        tiles[prevCoord!!.col][prevCoord!!.line]?.isAlreadySelected = false
    }

    private fun isTileAnOption(column: Int, row: Int): Boolean {
        for (option in options) {
            if (option!!.col == column && option.line == row)
                return true
        }
        return false
    }

    fun setOnBoardClickedListener(listener: BoardClickListener) {
        this.listener = listener
    }


    fun movePiece(prevCoord: Coord?, newCoord: Coord?, movedPiece: Piece?) {
        val prevCol = prevCoord!!.col
        val prevLine = prevCoord.line
        val newCol = newCoord!!.col
        val newLine = newCoord.line

        tiles[prevCol][prevLine]?.piecesType = null
        tiles[newCol][newLine]?.piecesType = Pair(movedPiece!!.army, movedPiece.piece)
        resetOptions()
        setOriginalColorToAllOptions()
    }

    private fun deselectPreviousPiece() {
        //remover a seleção da peça antiga
        setOriginalColor(
            prevCoord!!.line,
            prevCoord!!.col,
            tiles[prevCoord!!.col][prevCoord!!.line]!!
        )
        tiles[prevCoord!!.col][prevCoord!!.line]?.isAlreadySelected = false
    }

    /*private fun invertArmy(): Army {
        return if (newArmyToPlay == Army.WHITE) {
            Army.BLACK
        } else {
            Army.WHITE
        }
    }*/

    /**
     * set background color back to normal in all options
     */
    private fun setOriginalColorToAllOptions() {
        for (position in options)
            setOriginalColor(
                position!!.col,
                position.line,
                tiles[position.col][position.line]!!
            )
    }

    private fun setOriginalColor(row: Int, column: Int, tile: Tile) {
        when ((row + column) % 2) {
            0 -> changeBackgroundColor(
                tile,
                ctx.resources.getColor(R.color.chess_board_white, null)
            )

            1 -> changeBackgroundColor(
                tile,
                ctx.resources.getColor(R.color.chess_board_black, null)
            )
        }
    }

    private fun getAvailableOptions(piece: Piece): MutableList<Pair<Coord, Boolean>?> {
        return piece.searchRoute()
    }

    private fun changeBackgroundColor(tile: Tile, color: Int) {
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

    fun updateView(board: Array<Array<Piece?>>, newArmyToPlay: Army, isPreview: Boolean) {
        this.newArmyToPlay = newArmyToPlay
        for (column in 0..7) {
            for (line in 0..7) {
                val piece = board[column][line]
                if (piece != null)
                    tiles[column][line]?.piecesType = Pair(piece.army, piece.piece)
                else
                    tiles[column][line]?.piecesType = null
                if (isPreview)
                    tiles[column][line]?.setOnClickListener(null)
            }
        }
    }

    fun paintBoard(col: Int, line: Int) {
        changeBackgroundColor(tiles[col][line]!!, Color.GREEN)
    }

    fun updateOptions(availableOption: Coord?) {
        options.add(availableOption)
    }

    fun resetOptions() {
        options.clear()
    }

    fun updateCheckView() {
        //TODO: Get Last Piece
       // changeBackgroundColor(tiles[1][1]!!/*tiles[col][line]!!*/, Color.RED)
    }

    companion object {
        private const val COLUMNS = 8
        private const val LINES = 8
    }

}