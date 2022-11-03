package com.sakamata.chess.maintenance;

public class ChessConstants {

    public static final String FEN_START = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public static final String FEN_VALIDATION_REGEX = "^(((?:[rnbqkpRNBQKP1-8]{1,8}\\/){7})[rnbqkpRNBQKP1-8]{1,8})\\s([b|w])\\s([-|—]|([K|Q|k|q]{1,4}))(\\s([-|—]|[a-h][3|6]))?\\s?(\\d+\\s([1-9][0-9]*)+)?\\s?$";

    public static final int EMPTY = 0;
    public static final int ALL = 0;
    public static final int PAWN = 1;
    public static final int KNIGHT = 2;
    public static final int BISHOP = 3;
    public static final int ROOK = 4;
    public static final int QUEEN = 5;
    public static final int KING = 6;

    public static final int WHITE = 0;
    public static final int BLACK = 1;

    public static final int WK = 8;
    public static final int WQ = 4;
    public static final int BK = 2;
    public static final int BQ = 1;

    public static final char[][] ASCII_PIECES = {{'1', 'P', 'N', 'B', 'R', 'Q', 'K' }, {'1', 'p', 'n', 'b', 'r', 'q', 'k' }};

    public static final long[][] SEGMENTS = new long[64][64];
    public static final long[][] PINNED_MOVEMENT = new long[64][64];

    public static final int[] EN_PASSANT_SHIFT = { 8, -8 };

    static {
        int i;

        // fill from->to where to > from
        for (int from = 0; from < 64; from++) {
            for (int to = from + 1; to < 64; to++) {

                // horizontal
                if (from / 8 == to / 8) {
                    i = to - 1;
                    while (i > from) {
                        SEGMENTS[from][to] |= Square.getByIndex(i).bitboard;
                        i--;
                    }
                }

                // vertical
                if (from % 8 == to % 8) {
                    i = to - 8;
                    while (i > from) {
                        SEGMENTS[from][to] |= Square.getByIndex(i).bitboard;
                        i -= 8;
                    }
                }

                // diagonal \
                if ((to - from) % 9 == 0 && to % 8 > from % 8) {
                    i = to - 9;
                    while (i > from) {
                        SEGMENTS[from][to] |= Square.getByIndex(i).bitboard;
                        i -= 9;
                    }
                }

                // diagonal /
                if ((to - from) % 7 == 0 && to % 8 < from % 8) {
                    i = to - 7;
                    while (i > from) {
                        SEGMENTS[from][to] |= Square.getByIndex(i).bitboard;
                        i -= 7;
                    }
                }
            }
        }

        // fill from->to where to < from
        for (int from = 0; from < 64; from++) {
            for (int to = 0; to < from; to++) {
                SEGMENTS[from][to] = SEGMENTS[to][from];
            }
        }
    }

    static {
        int[] DIRECTION = { -1, -7, -8, -9, 1, 7, 8, 9 };
        // PINNED MOVEMENT, x-ray from the king to the pinned-piece and beyond
        for (int pinnedPieceIndex = 0; pinnedPieceIndex < 64; pinnedPieceIndex++) {
            for (int kingIndex = 0; kingIndex < 64; kingIndex++) {
                int correctDirection = 0;
                for (int direction : DIRECTION) {
                    if (correctDirection != 0) {
                        break;
                    }
                    int xray = kingIndex + direction;
                    while (xray >= 0 && xray < 64) {
                        if (direction == -1 || direction == -9 || direction == 7) {
                            if ((xray & 7) == 7) {
                                break;
                            }
                        }
                        if (direction == 1 || direction == 9 || direction == -7) {
                            if ((xray & 7) == 0) {
                                break;
                            }
                        }
                        if (xray == pinnedPieceIndex) {
                            correctDirection = direction;
                            break;
                        }
                        xray += direction;
                    }
                }

                if (correctDirection != 0) {
                    int xray = kingIndex + correctDirection;
                    while (xray >= 0 && xray < 64) {
                        if (correctDirection == -1 || correctDirection == -9 || correctDirection == 7) {
                            if ((xray & 7) == 7) {
                                break;
                            }
                        }
                        if (correctDirection == 1 || correctDirection == 9 || correctDirection == -7) {
                            if ((xray & 7) == 0) {
                                break;
                            }
                        }
                        PINNED_MOVEMENT[pinnedPieceIndex][kingIndex] |= Square.getByIndex(xray).bitboard;
                        xray += correctDirection;
                    }
                }
            }
        }
    }
    
}
