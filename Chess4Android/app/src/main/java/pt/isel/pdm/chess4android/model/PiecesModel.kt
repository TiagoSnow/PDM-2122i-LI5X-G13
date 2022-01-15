package pt.isel.pdm.chess4android.model

enum class Army {
    WHITE, BLACK;

    companion object {
        val firstToMove: Army = Army.WHITE
    }

    val other: Army
        get() = if(this == Army.WHITE) Army.BLACK else Army.WHITE

}
enum class PiecesType {
    PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
}

data class Square(val col: Int, val line: Int)




