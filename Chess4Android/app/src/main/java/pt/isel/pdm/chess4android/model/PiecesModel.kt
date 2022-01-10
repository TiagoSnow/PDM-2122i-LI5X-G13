package pt.isel.pdm.chess4android.model

enum class Army { WHITE, BLACK }
enum class PiecesType {
    PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
}

data class Square(val col: Int, val line: Int)




