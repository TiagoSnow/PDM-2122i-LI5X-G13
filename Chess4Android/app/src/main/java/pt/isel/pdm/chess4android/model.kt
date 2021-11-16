package pt.isel.pdm.chess4android

enum class Army { WHITE, BLACK }
enum class Piece {
    PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
}

data class PieceId(val army: Boolean, val piece: Piece)


