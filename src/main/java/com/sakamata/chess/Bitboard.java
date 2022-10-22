package com.sakamata.chess;

public class Bitboard {

    public static final long RANK_2 = 0xff00L;
    public static final long RANK_7 = 0xff000000000000L;
    public static final long[] RANK_NON_PROMOTION = { ~RANK_7, ~RANK_2 };
    public static final long NOT_FILE_A = 0x7f7f7f7f7f7f7f7fL;
    public static final long NOT_FILE_H = 0xfefefefefefefefeL;

    public static final long RANK_23456 = 0xffffffffff00L;
    public static final long RANK_34567 = 0xffffffffff0000L;

    public static final long C1 = 0x20L;
    public static final long G1 = 0x2L;
    public static final long C8 = 0x2000000000000000L;
    public static final long G8 = 0x200000000000000L;
    public static final long C1_G1 = 0x22L;
    public static final long C8_G8 = 0x2200000000000000L;
    public static final long F1_G1 = 0x6L;
    public static final long B1C1D1 = 0x70L;
    public static final long F8_G8 = 0x600000000000000L;
    public static final long B8C8D8 = 0x7000000000000000L;

    private static final byte[] DE_BRUIJN_TABLE = {
            0, 1, 48, 2, 57, 49, 28, 3,
            61, 58, 50, 42, 38, 29, 17, 4,
            62, 55, 59, 36, 53, 51, 43, 22,
            45, 39, 33, 30, 24, 18, 12, 5,
            63, 47, 56, 27, 60, 41, 37, 16,
            54, 35, 52, 21, 44, 32, 23, 11,
            46, 26, 40, 15, 34, 20, 31, 10,
            25, 14, 19, 9, 13, 8, 7, 6};

    private static final long DE_BRUIJN_CONST = 0x03F79D71B4CB0A89L;

    public static long toBit(int i) {
        return 1L << i;
    }

    public static long getMSBit(long n) {
        n |= (n >> 1);
        n |= (n >> 2);
        n |= (n >> 4);
        n |= (n >> 8);
        n |= (n >> 16);
        n |= (n >> 32);
        return n - (n >>> 1);
    }

    public static byte indexOfMSBit(long n) {
        return DE_BRUIJN_TABLE[(int) ((getMSBit(n) * DE_BRUIJN_CONST) >>> 58)];
    }

    public static byte indexOfLSBit(long n) {
        return DE_BRUIJN_TABLE[(int) (((n & -n) * DE_BRUIJN_CONST) >>> 58)];
    }

    public static long getWhitePawnAttacks(final long pawns) {
        return pawns << 9 & Bitboard.NOT_FILE_H | pawns << 7 & Bitboard.NOT_FILE_A;
    }

    public static long getBlackPawnAttacks(final long pawns) {
        return pawns >>> 9 & Bitboard.NOT_FILE_A | pawns >>> 7 & Bitboard.NOT_FILE_H;
    }

}
