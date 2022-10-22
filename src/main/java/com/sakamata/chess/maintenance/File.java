package com.sakamata.chess.maintenance;

public enum File {

    H,
    G,
    F,
    E,
    D,
    C,
    B,
    A;

    public final byte ind;
    public final long bitboard;

    File() {
        ind = (byte) ordinal();
        bitboard = 0x0101010101010101L << ordinal();
    }

    public static File getByIndex(int sqrInd) {
        return values()[sqrInd & 7];
    }

}
