package pt.isel.pdm.chess4android.model

import pt.isel.pdm.chess4android.pieces.Coord

class MultiplayerModel () : GameModel() {

    lateinit var checkPath: MutableList<Pair<Coord, Boolean>>

    fun switchArmy() {
        newArmyToPlay = if(newArmyToPlay == Army.WHITE) {
            Army.BLACK
        } else {
            Army.WHITE
        }
    }

}