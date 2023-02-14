package com.sakamata.chess.evaluation;

import com.sakamata.chess.ChessBoard;
import com.sakamata.chess.maintenance.Square;
import com.sakamata.chess.move.MoveEncoder;
import com.sakamata.chess.move.PrecalculatedMoves;

import static com.sakamata.chess.evaluation.EvaluationConstants.MATERIAL;
import static com.sakamata.chess.evaluation.EvaluationConstants.PROMOTION_SCORE;
import static com.sakamata.chess.maintenance.ChessConstants.*;

public class SEERecursive {

    private static int getSmallestAttackSeeMove(final long[] pieces, final int sideToMove, final int toIndex,
                                                final long allPieces, final long slidingMask) {

        // put 'super-piece' in see position
        long attackMove;

        // pawn non-promotion attacks
        attackMove = PrecalculatedMoves.PAWN_ATTACKS[sideToMove ^ 1][toIndex] & pieces[PAWN] & allPieces;
        if (attackMove != 0) {
            return Long.numberOfTrailingZeros(attackMove);
        }

        // knight attacks
        attackMove = pieces[KNIGHT] & PrecalculatedMoves.KNIGHT_MOVES[toIndex] & allPieces;
        if (attackMove != 0) {
            return Long.numberOfTrailingZeros(attackMove);
        }

        // bishop attacks
        if ((pieces[BISHOP] & slidingMask) != 0) {
            attackMove = pieces[BISHOP] & PrecalculatedMoves.getBishopAttack(toIndex, allPieces) & allPieces;
            if (attackMove != 0) {
                return Long.numberOfTrailingZeros(attackMove);
            }
        }

        // rook attacks
        if ((pieces[ROOK] & slidingMask) != 0) {
            attackMove = pieces[ROOK] & PrecalculatedMoves.getRookAttack(toIndex, allPieces) & allPieces;
            if (attackMove != 0) {
                return Long.numberOfTrailingZeros(attackMove);
            }
        }

        // queen attacks
        if ((pieces[QUEEN] & slidingMask) != 0) {
            attackMove = pieces[QUEEN] & PrecalculatedMoves.getQueenAttack(toIndex, allPieces) & allPieces;
            if (attackMove != 0) {
                return Long.numberOfTrailingZeros(attackMove);
            }
        }

        // king attacks
        attackMove = pieces[KING] & PrecalculatedMoves.KING_MOVES[toIndex];
        if (attackMove != 0) {
            return Long.numberOfTrailingZeros(attackMove);
        }

        return -1;
    }

    private static int getSeeScore(final ChessBoard board, final int sideToMove, final int toIndex, final int attackedPieceIndex, long allPieces,
                                   long slidingMask) {

        final int fromIndex = getSmallestAttackSeeMove(board.pieces[sideToMove], sideToMove, toIndex, allPieces, slidingMask);

        if (fromIndex == -1) {
            return 0;
        }
        if (attackedPieceIndex == KING) {
            return 3000;
        }

        allPieces ^= Square.getByIndex(fromIndex).bitboard;
        slidingMask &= allPieces;

        return Math.max(0,
                MATERIAL[attackedPieceIndex] - getSeeScore(board, sideToMove ^ 1, toIndex, board.piecesIndexBoard[fromIndex],
                        allPieces, slidingMask));
    }

    public static int getSeeCaptureScore(final ChessBoard board, final int move) {

        final int index = MoveEncoder.getToIndex(move);
        final long allPieces = board.allPieces & ~Square.getByIndex(MoveEncoder.getFromIndex(move)).bitboard;
        final long slidingMask = PrecalculatedMoves.getQueenAttack(index, 0L) & allPieces;

        // add score when promotion
        if (MoveEncoder.isPromotion(move)) {
            return PROMOTION_SCORE[MoveEncoder.getMoveType(move)] + MATERIAL[MoveEncoder.getAttackedPieceIndex(move)]
                    - getSeeScore(board, board.sideToMoveInverse, index, MoveEncoder.getMoveType(move), allPieces, slidingMask);
        } else {
            return MATERIAL[MoveEncoder.getAttackedPieceIndex(move)]
                    - getSeeScore(board, board.sideToMoveInverse, index, MoveEncoder.getSourcePieceIndex(move), allPieces, slidingMask);
        }

    }
}
