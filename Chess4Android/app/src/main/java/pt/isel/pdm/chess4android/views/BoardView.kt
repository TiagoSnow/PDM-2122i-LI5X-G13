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
import pt.isel.pdm.chess4android.pieces.Coord
import pt.isel.pdm.chess4android.pieces.Piece

/**
 * Custom view that implements a chess board.
 */

typealias TileTouchListener = (tile: Tile, row: Int, column: Int) -> Unit

@SuppressLint("ClickableViewAccessibility")
class BoardView(private val ctx: Context, attrs: AttributeSet?) : GridLayout(ctx, attrs) {

    private val side = 8

    private var tiles: Array<Array<Tile?>> = Array(COLUMNS) {
        Array(LINES) { null }
    }
    private lateinit var board: Array<Array<Piece?>>
    private var options: MutableList<Pair<Coord, Boolean>?> = mutableListOf()
    private var prevCoord: Coord? = null
    private var newArmyToPlay: Army = Army.WHITE

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

                if(board[column][row]?.army != newArmyToPlay && board[column][row] != null) {
                    //Clean all options selection
                    setOriginalColorToAllOptions()
                    //Clean prior selected piece color
                    setOriginalColor(prevCoord!!.line, prevCoord!!.col, tiles[prevCoord!!.col][prevCoord!!.line]!!)
                    //Clean prior selected piece
                    tiles[prevCoord!!.col][prevCoord!!.line]?.isAlreadySelected = false
                    //Clean all available options
                    options = mutableListOf()
                    return@setOnClickListener
                }

                //Apaga as options independentemente do sitio do próximo clique
                setOriginalColorToAllOptions()

                if (tile.isAlreadySelected) {
                    setOriginalColor(row, column, tile)
                    tile.isAlreadySelected = false
                    options = mutableListOf()
                } else {
                    if (options.isNotEmpty()) {
                        for (option in options) {
                            //Caso encontre uma option no clique
                            if (option?.first?.col == column && option.first.line == row) {
                                val movedPiece = board[prevCoord!!.col][prevCoord!!.line]

                                //Atualizar coords da peça movida
                                movedPiece?.col = column
                                movedPiece?.line = row

                                //Atualizar model
                                board[column][row] = movedPiece
                                board[prevCoord!!.col][prevCoord!!.line] = null

                                //Atualizar view
                                tiles[column][row]?.piecesType =
                                    Pair(movedPiece!!.army, movedPiece.piece)
                                tiles[prevCoord!!.col][prevCoord!!.line]?.piecesType = null

                                //remover a seleção da peça antiga
                                setOriginalColor(
                                    prevCoord!!.line,
                                    prevCoord!!.col,
                                    tiles[prevCoord!!.col][prevCoord!!.line]!!
                                )
                                tiles[prevCoord!!.col][prevCoord!!.line]?.isAlreadySelected = false

                                options = mutableListOf()
                                newArmyToPlay = invertArmy()
                                return@setOnClickListener
                            }
                        }
                    }

                    if (board[column][row] != null) {

                        //Aparecimento dos Caminhos Possíveis
                        options = getAvailableOptions(row, column)
                        for (path in options) {
                            changeBackgroundColor(
                                tiles[path!!.first.col][path!!.first.line]!!,
                                Color.RED
                            )
                        }

                        if (prevCoord == null)
                            prevCoord = Coord(column, row)
                        else {
                            //remover a seleção da peça antiga && Verificação de colisao de options
                            var flag = false
                            for (option in options) {
                                if (option?.first?.col == prevCoord!!.col && option?.first?.line == prevCoord!!.line) {
                                    flag = true
                                    break
                                }
                            }
                            if (!flag) {
                                setOriginalColor(
                                    prevCoord!!.line,
                                    prevCoord!!.col,
                                    tiles[prevCoord!!.col][prevCoord!!.line]!!
                                )
                            }
                            tiles[prevCoord!!.col][prevCoord!!.line]?.isAlreadySelected = false
                        }
                        prevCoord?.col = column
                        prevCoord?.line = row

                        //Colocar cor verde na atual
                        changeBackgroundColor(tile, Color.GREEN)
                        tile.isAlreadySelected = true
                    } else {
                        //remover a seleção no clique da peça vazia
                        setOriginalColor(
                            prevCoord!!.line,
                            prevCoord!!.col,
                            tiles[prevCoord!!.col][prevCoord!!.line]!!
                        )
                        tiles[prevCoord!!.col][prevCoord!!.line]?.isAlreadySelected = false
                        tile.isAlreadySelected = true
                        prevCoord?.col = column
                        prevCoord?.line = row

                    }
                }
                Log.v("App", row.toString() + " : " + column)
            }
            addView(tile)
            tiles[column][row] = tile

        }
    }

    private fun invertArmy(): Army {
        return if(newArmyToPlay == Army.WHITE) { Army.BLACK } else { Army.WHITE }
    }

    private fun setOriginalColorToAllOptions() {
        //set background color back to normal in all options
        for (position in options)
            setOriginalColor(
                position!!.first.col,
                position.first.line,
                tiles[position.first.col][position.first.line]!!
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

    private fun getAvailableOptions(row: Int, column: Int): MutableList<Pair<Coord, Boolean>?> {
        val piece = board[column][row]
        if (piece != null)
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

    fun updateView(board: Array<Array<Piece?>>, newArmyToPlay: Army) {
        this.board = board
        this.newArmyToPlay = newArmyToPlay
        for (column in 0..7) {
            for (line in 0..7) {
                val piece = board[column][line]
                if (piece != null)
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