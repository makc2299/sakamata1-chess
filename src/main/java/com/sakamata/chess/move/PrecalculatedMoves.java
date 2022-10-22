package com.sakamata.chess.move;

import com.sakamata.chess.maintenance.File;
import com.sakamata.chess.maintenance.Square;

import static com.sakamata.chess.maintenance.ChessConstants.BLACK;
import static com.sakamata.chess.maintenance.ChessConstants.WHITE;

public class PrecalculatedMoves {

    public static final long[] KNIGHT_MOVES = new long[64];
    public static final long[] KING_MOVES = new long[64];

    public static final long[][] PAWN_ATTACKS = new long[2][64];

    private static final long[] rookRelevantOccupancies = new long[64];
    private static final long[] bishopRelevantOccupancies = new long[64];
    private static final long[][] rookMagicMoves = new long[64][];
    private static final long[][] bishopMagicMoves = new long[64][];

    static {
        for (Square square : Square.values()) {
            PAWN_ATTACKS[WHITE][square.ind] = (square.bitboard << 7 & ~File.A.bitboard) | (square.bitboard << 9 & ~File.H.bitboard);
            PAWN_ATTACKS[BLACK][square.ind] = (square.bitboard >>> 7 & ~File.H.bitboard) | (square.bitboard >>> 9 & ~File.A.bitboard);
        }
    }

    static {
        for (Square square : Square.values()) {
            long knightMove = 0L;

            knightMove |= square.bitboard << 10 & ~(File.G.bitboard | File.H.bitboard);
            knightMove |= square.bitboard << 17 & ~File.H.bitboard;
            knightMove |= square.bitboard << 15 & ~File.A.bitboard;
            knightMove |= square.bitboard << 6 & ~(File.A.bitboard | File.B.bitboard);

            knightMove |= square.bitboard >>> 10 & ~(File.A.bitboard | File.B.bitboard);
            knightMove |= square.bitboard >>> 17 & ~File.A.bitboard;
            knightMove |= square.bitboard >>> 15 & ~File.H.bitboard;
            knightMove |= square.bitboard >>> 6 & ~(File.G.bitboard | File.H.bitboard);

            KNIGHT_MOVES[square.ind] = knightMove;
        }
    }

    static {
        for (Square square : Square.values()) {
            long kingMove = 0L;

            kingMove |= square.bitboard << 1 & ~File.H.bitboard;
            kingMove |= square.bitboard << 9 & ~File.H.bitboard;
            kingMove |= square.bitboard << 8;
            kingMove |= square.bitboard << 7 & ~File.A.bitboard;

            kingMove |= square.bitboard >>> 1 & ~File.A.bitboard;
            kingMove |= square.bitboard >>> 9 & ~File.A.bitboard;
            kingMove |= square.bitboard >>> 8;
            kingMove |= square.bitboard >>> 7 & ~File.H.bitboard;

            KING_MOVES[square.ind] = kingMove;
        }
    }

    static {
        for (int square = 0; square < 64; square++) {
            rookRelevantOccupancies[square] = MagicUtil.getRookRelevantOccupancyMask(square);
            int bitNumber = MagicUtil.rookOccupancyBits[square];
            rookMagicMoves[square] = new long[1 << bitNumber];

            for (int index = 0; index < (1 << bitNumber); index++) {
                long occupancy = MagicUtil.getOccupancyVariation(index, bitNumber, rookRelevantOccupancies[square]);
                int magicIndex = (int) ((occupancy * MagicUtil.rookMagicNumbers[square]) >>> (64 - bitNumber));

                rookMagicMoves[square][magicIndex] = MagicUtil.getRookAttackMask(square, occupancy);
            }
        }
    }

    static {
        for (int square = 0; square < 64; square++) {
            bishopRelevantOccupancies[square] = MagicUtil.getBishopRelevantOccupancyMask(square);
            int bitNumber = MagicUtil.bishopOccupancyBits[square];
            bishopMagicMoves[square] = new long[1 << bitNumber];

            for (int index = 0; index < (1 << bitNumber); index++) {
                long occupancy = MagicUtil.getOccupancyVariation(index, bitNumber, bishopRelevantOccupancies[square]);
                int magicIndex = (int) ((occupancy * MagicUtil.bishopMagicNumbers[square]) >>> (64 - bitNumber));

                bishopMagicMoves[square][magicIndex] = MagicUtil.getBishopAttackMask(square, occupancy);
            }
        }
    }

    public static long getRookAttack(int square, long occupancy) {
        return rookMagicMoves[square][(int)((occupancy & rookRelevantOccupancies[square]) *
                MagicUtil.rookMagicNumbers[square] >>> (64 - MagicUtil.rookOccupancyBits[square]))];
    }

    public static long getBishopAttack(int square, long occupancy) {
        return bishopMagicMoves[square][(int)((occupancy & bishopRelevantOccupancies[square]) *
                MagicUtil.bishopMagicNumbers[square] >>> (64 - MagicUtil.bishopOccupancyBits[square]))];
    }

    public static long getQueenAttack(int square, long occupancy) {
        return rookMagicMoves[square][(int)((occupancy & rookRelevantOccupancies[square]) *
                MagicUtil.rookMagicNumbers[square] >>> (64 - MagicUtil.rookOccupancyBits[square]))] |
                bishopMagicMoves[square][(int)((occupancy & bishopRelevantOccupancies[square]) *
                        MagicUtil.bishopMagicNumbers[square] >>> (64 - MagicUtil.bishopOccupancyBits[square]))];
    }
}
