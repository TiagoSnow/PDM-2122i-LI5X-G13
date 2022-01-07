package pt.isel.pdm.chess4android

enum class Army { WHITE, BLACK }
enum class PiecesType {
    PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
}

data class Square(val col: Int, val line: Int)




