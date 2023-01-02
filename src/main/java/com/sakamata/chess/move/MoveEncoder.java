package com.sakamata.chess.move;

import static com.sakamata.chess.maintenance.ChessConstants.*;

public class MoveEncoder {

    // move types
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_EP = 1;
    public static final int TYPE_PROMOTION_N = KNIGHT;
    public static final int TYPE_PROMOTION_B = BISHOP;
    public static final int TYPE_PROMOTION_R = ROOK;
    public static final int TYPE_PROMOTION_Q = QUEEN;
    public static final int TYPE_CASTLING = 6;

    // shifts
    private static final int SHIFT_TO = 6; // 6
    private static final int SHIFT_SOURCE = 12; // 3
    private static final int SHIFT_ATTACK = 15; // 3
    private static final int SHIFT_MOVE_TYPE = 18; // 3
    private static final int SHIFT_PROMOTION = 21; // 1
    private static final int SHIFT_CASTLING = 22; // 4

    // masks
    private static final int MASK_3_BITS = 7; // 3
    private static final int MASK_4_BITS = 15; // 4
    private static final int MASK_6_BITS = 0x3f; // 6
    private static final int MASK_12_BITS = 0xfff;

    private static final int MASK_ATTACK = 7 << SHIFT_ATTACK; // 3
    private static final int MASK_PROMOTION = 1 << SHIFT_PROMOTION; // 1
    private static final int MASK_QUIET = MASK_PROMOTION | MASK_ATTACK;

    public static int getFromIndex(final int move) {
        return move & MASK_6_BITS;
    }

    public static int getToIndex(final int move) {
        return move >>> SHIFT_TO & MASK_6_BITS;
    }

    public static int getFromToIndex(final int move) {
        return move & MASK_12_BITS;
    }

    public static int getSourcePieceIndex(final int move) {
        return move >>> SHIFT_SOURCE & MASK_3_BITS;
    }

    public static int getAttackedPieceIndex(final int move) {
        return move >>> SHIFT_ATTACK & MASK_3_BITS;
    }

    public static int getMoveType(final int move) {
        return move >>> SHIFT_MOVE_TYPE & MASK_3_BITS;
    }

    public static int getCastling(final int move) {
        return move >>> SHIFT_CASTLING & MASK_4_BITS;
    }

    public static int createMove(final int fromIndex, final int toIndex, final int sourcePieceIndex) {
        return sourcePieceIndex << SHIFT_SOURCE | toIndex << SHIFT_TO | fromIndex;
    }

    public static int createKingMove(final int fromIndex, final int toIndex, final int castlingRights) {
        return castlingRights << SHIFT_CASTLING | KING << SHIFT_SOURCE | toIndex << SHIFT_TO | fromIndex;
    }

    public static int createRookMove(final int fromIndex, final int toIndex, final int castlingRights) {
        return castlingRights << SHIFT_CASTLING | ROOK << SHIFT_SOURCE | toIndex << SHIFT_TO | fromIndex;
    }

    public static int createWhitePawnMove(final int fromIndex) {
        return PAWN << SHIFT_SOURCE | (fromIndex + 8) << SHIFT_TO | fromIndex;
    }

    public static int createBlackPawnMove(final int fromIndex) {
        return PAWN << SHIFT_SOURCE | (fromIndex - 8) << SHIFT_TO | fromIndex;
    }

    public static int createWhitePawn2Move(final int fromIndex) {
        return PAWN << SHIFT_SOURCE | (fromIndex + 16) << SHIFT_TO | fromIndex;
    }

    public static int createBlackPawn2Move(final int fromIndex) {
        return PAWN << SHIFT_SOURCE | (fromIndex - 16) << SHIFT_TO | fromIndex;
    }

    public static int createPromotionMove(final int promotionPiece, final int fromIndex, final int toIndex) {
        return 1 << SHIFT_PROMOTION | promotionPiece << SHIFT_MOVE_TYPE | PAWN << SHIFT_SOURCE | toIndex << SHIFT_TO | fromIndex;
    }

    public static int createAttackMove(final int fromIndex, final int toIndex, final int sourcePieceIndex,
                                       final int attackedPieceIndex, final int castlingRights) {
        return castlingRights << SHIFT_CASTLING | attackedPieceIndex << SHIFT_ATTACK | sourcePieceIndex << SHIFT_SOURCE |
                toIndex << SHIFT_TO | fromIndex;
    }

    public static int createPromotionAttack(final int promotionPiece, final int fromIndex, final int toIndex,
                                            final int attackedPieceIndex, final int castlingRights) {
        return castlingRights << SHIFT_CASTLING | 1 << SHIFT_PROMOTION | promotionPiece << SHIFT_MOVE_TYPE |
                attackedPieceIndex << SHIFT_ATTACK | PAWN << SHIFT_SOURCE | toIndex << SHIFT_TO | fromIndex;
    }

    public static int createEPMove(final int fromIndex, final int toIndex) {
        return TYPE_EP << SHIFT_MOVE_TYPE | PAWN << SHIFT_ATTACK | PAWN << SHIFT_SOURCE | toIndex << SHIFT_TO | fromIndex;
    }

    public static int createCastlingMove(final int fromIndex, final int toIndex, final int castlingRights) {
        return castlingRights << SHIFT_CASTLING | TYPE_CASTLING << SHIFT_MOVE_TYPE | KING << SHIFT_SOURCE |
                toIndex << SHIFT_TO | fromIndex;
    }

    public static boolean isPromotion(final int move) {
        return (move & MASK_PROMOTION) != 0;
    }

    public static boolean isPawnPush78(final int move) {
        return getSourcePieceIndex(move) == PAWN && (getToIndex(move) > 47 || getToIndex(move) < 16);
    }

    public static boolean isQuiet(final int move) {
        return (move & MASK_QUIET) == 0;
    }

    public static boolean isCapture(final int move) {
        return (move & MASK_ATTACK) != 0 || getMoveType(move) == TYPE_EP;
    }

    public static boolean isNormalMove(final int move) {
        return getMoveType(move) == TYPE_NORMAL;
    }

    public static boolean isEPMove(final int move) {
        return getMoveType(move) == TYPE_EP;
    }

    public static boolean isCastlingMove(int move) {
        return getMoveType(move) == TYPE_CASTLING;
    }

}

