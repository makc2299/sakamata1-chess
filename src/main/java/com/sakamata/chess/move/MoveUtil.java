package com.sakamata.chess.move;

import com.sakamata.chess.ChessBoard;
import com.sakamata.chess.maintenance.Square;

import static com.sakamata.chess.maintenance.ChessConstants.*;

public class MoveUtil {

    public static boolean isSquareAttacked(ChessBoard board, int square) {
        if ((board.pieces[board.sideToMoveInverse][PAWN] & PrecalculatedMoves.PAWN_ATTACKS[board.sideToMove][square]) != 0)
            return true;
        if ((board.pieces[board.sideToMoveInverse][KNIGHT] & PrecalculatedMoves.KNIGHT_MOVES[square]) != 0)
            return true;
        if (((board.pieces[board.sideToMoveInverse][ROOK] | board.pieces[board.sideToMoveInverse][QUEEN]) &
                PrecalculatedMoves.getRookAttack(square, board.allPieces)) != 0)
            return true;
        if (((board.pieces[board.sideToMoveInverse][BISHOP] | board.pieces[board.sideToMoveInverse][QUEEN]) &
                PrecalculatedMoves.getBishopAttack(square, board.allPieces)) != 0)
            return true;
        return (board.pieces[board.sideToMoveInverse][KING] & PrecalculatedMoves.KING_MOVES[square]) != 0;
    }

    public static boolean isInCheck(final int kingSquare, final int side, final long[] enemyPieces, final long allPieces) {
        if ((enemyPieces[PAWN] & PrecalculatedMoves.PAWN_ATTACKS[side][kingSquare]) != 0)
            return true;
        if ((enemyPieces[KNIGHT] & PrecalculatedMoves.KNIGHT_MOVES[kingSquare]) != 0)
            return true;
        if (((enemyPieces[ROOK] | enemyPieces[QUEEN]) & PrecalculatedMoves.getRookAttack(kingSquare, allPieces)) != 0)
            return true;
        return ((enemyPieces[BISHOP] | enemyPieces[QUEEN]) & PrecalculatedMoves.getBishopAttack(kingSquare, allPieces)) != 0;
    }

    public static long getCheckingPieces(ChessBoard board, int kingSquare) {
        return (board.pieces[board.sideToMoveInverse][PAWN] & PrecalculatedMoves.PAWN_ATTACKS[board.sideToMove][kingSquare]) |
                (board.pieces[board.sideToMoveInverse][KNIGHT] & PrecalculatedMoves.KNIGHT_MOVES[kingSquare]) |
                ((board.pieces[board.sideToMoveInverse][ROOK] | board.pieces[board.sideToMoveInverse][QUEEN]) &
                        PrecalculatedMoves.getRookAttack(kingSquare, board.allPieces)) |
                ((board.pieces[board.sideToMoveInverse][BISHOP] | board.pieces[board.sideToMoveInverse][QUEEN]) &
                        PrecalculatedMoves.getBishopAttack(kingSquare, board.allPieces));
    }

    public static boolean isLegalEPMove(final ChessBoard board, final int fromIndex) {
        if (board.epIndex == 0) {
            return false;
        }

        final int sideToMove = board.sideToMove;
        final int inverseSideToMove = board.sideToMoveInverse;
        board.pieces[inverseSideToMove][PAWN] ^= Square.getByIndex(board.epIndex + EN_PASSANT_SHIFT[inverseSideToMove]).bitboard;

        final boolean isInCheck = isInCheck(board.kingIndex[sideToMove], sideToMove, board.pieces[inverseSideToMove],
                board.pieces[sideToMove][ALL] ^ Square.getByIndex(fromIndex).bitboard ^ Square.getByIndex(board.epIndex).bitboard
                        | board.pieces[inverseSideToMove][ALL] ^ Square.getByIndex(board.epIndex + EN_PASSANT_SHIFT[inverseSideToMove]).bitboard);

        board.pieces[inverseSideToMove][PAWN] ^= Square.getByIndex(board.epIndex + EN_PASSANT_SHIFT[inverseSideToMove]).bitboard;

        return !isInCheck;
    }
}
