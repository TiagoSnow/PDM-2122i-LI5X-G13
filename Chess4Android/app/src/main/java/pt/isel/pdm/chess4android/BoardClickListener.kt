package pt.isel.pdm.chess4android

import pt.isel.pdm.chess4android.pieces.Coord

interface BoardClickListener {

    fun onTileClicked(col: Int, line:Int)

    fun onMovement(prevCoord: Coord?, newCoord: Coord?);
    fun onCheckmate()
}