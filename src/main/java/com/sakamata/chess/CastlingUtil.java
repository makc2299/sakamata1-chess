package com.sakamata.chess;

import com.sakamata.chess.maintenance.Square;
import com.sakamata.chess.move.MoveUtil;

import static com.sakamata.chess.maintenance.ChessConstants.*;

public class CastlingUtil {

    // KQkq
    public static long getCastlingIndexes(final ChessBoard board) {
        if (board.castlingRights == 0) {
            return 0;
        }
        if (board.sideToMove == WHITE) {
            switch (board.castlingRights) {
                case 0:
                case 1:
                case 2:
                case 3:
                    return 0;
                case 4:
                case 5:
                case 6:
                case 7:
                    return Bitboard.C1;
                case 8:
                case 9:
                case 10:
                case 11:
                    return Bitboard.G1;
                case 12:
                case 13:
                case 14:
                case 15:
                    return Bitboard.C1_G1;
            }
        } else {
            switch (board.castlingRights) {
                case 0:
                case 4:
                case 8:
                case 12:
                    return 0;
                case 1:
                case 5:
                case 9:
                case 13:
                    return Bitboard.C8;
                case 2:
                case 6:
                case 10:
                case 14:
                    return Bitboard.G8;
                case 3:
                case 7:
                case 11:
                case 15:
                    return Bitboard.C8_G8;
            }
        }
        throw new RuntimeException("Unknown castling-right: " + board.castlingRights);
    }

    public static long getRookInBetweenIndex(final int castlingIndex) {
        switch (castlingIndex) {
            case 1:
                return Bitboard.F1_G1;
            case 5:
                return Bitboard.B1C1D1;
            case 57:
                return Bitboard.F8_G8;
            case 61:
                return Bitboard.B8C8D8;
        }
        throw new RuntimeException("Incorrect castling-index: " + castlingIndex);
    }

    public static boolean isValidCastlingMove(final ChessBoard board, final int fromIndex, final int toIndex) {
        if (board.checkingPieces != 0) {
            return false;
        }
        if ((board.allPieces & getRookInBetweenIndex(toIndex)) != 0) {
            return false;
        }

        long kingIndexes = SEGMENTS[fromIndex][toIndex] | Square.getByIndex(toIndex).bitboard;
        while (kingIndexes != 0) {
            // king does not move through a checked position?
            if (MoveUtil.isSquareAttacked(board, Long.numberOfTrailingZeros(kingIndexes))) {
                return false;
            }
            kingIndexes &= kingIndexes - 1;
        }

        return true;
    }

    public static int getCastlingRightsAfterRookMovedOrAttacked(final int castlingRights, final int rookIndex) {
        return switch (rookIndex) {
            case 0 -> castlingRights & 7; // 0111
            case 7 -> castlingRights & 11; // 1011
            case 56 -> castlingRights & 13; // 1101
            case 63 -> castlingRights & 14;
            default -> // 1110
                    castlingRights;
        };
    }

    public static int getCastlingRightsAfterKingMoved(final int castlingRights, final int kingIndex) {
        return switch (kingIndex) {
            case 3 -> castlingRights & 3; // 0011
            case 59 -> castlingRights & 12;
            default -> // 1100
                    castlingRights;
        };
    }

    public static void castleRookUpdate(final ChessBoard cb, final int kingToIndex) {
        switch (kingToIndex) {
            case 1:
                // white rook from 0 to 2
                updateRookPosition(cb, 0, 2, WHITE);
                return;
            case 57:
                // black rook from 56 to 58
                updateRookPosition(cb, 56, 58, BLACK);
                return;
            case 5:
                // white rook from 7 to 4
                updateRookPosition(cb, 7, 4, WHITE);
                return;
            case 61:
                // black rook from 63 to 60
                updateRookPosition(cb, 63, 60, BLACK);
                return;
        }
        throw new RuntimeException("Incorrect king castling to-index: " + kingToIndex);
    }

    public static void uncastleRookUpdate(final ChessBoard cb, final int kingToIndex) {
        switch (kingToIndex) {
            case 1:
                // white rook from 2 to 0
                updateRookPosition(cb, 2, 0, WHITE);
                return;
            case 57:
                // black rook from 58 to 56
                updateRookPosition(cb, 58, 56, BLACK);
                return;
            case 5:
                // white rook from 4 to 7
                updateRookPosition(cb, 4, 7, WHITE);
                return;
            case 61:
                // black rook from 60 to 63
                updateRookPosition(cb, 60, 63, BLACK);
                return;
        }
        throw new RuntimeException("Incorrect king castling to-index: " + kingToIndex);
    }

    private static void updateRookPosition(final ChessBoard board, final int fromIndex, final int toIndex, final int color) {
        board.pieces[color][ALL] ^= Square.getByIndex(fromIndex).bitboard | Square.getByIndex(toIndex).bitboard;
        board.pieces[color][ROOK] ^= Square.getByIndex(fromIndex).bitboard | Square.getByIndex(toIndex).bitboard;
        board.piecesIndexBoard[fromIndex] = EMPTY;
        board.piecesIndexBoard[toIndex] = ROOK;
    }
}
