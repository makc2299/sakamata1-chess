package com.sakamata.chess.maintenance;

public enum Rank {

    R1,
    R2,
    R3,
    R4,
    R5,
    R6,
    R7,
    R8;

    public final byte ind;
    public final long bitboard;

    Rank() {
        ind = (byte) ordinal();
        bitboard = 0x00000000000000FFL << (8 * ordinal());
    }

    public static Rank getByIndex(int sqrInd) {
        return values()[sqrInd >>> 3];
    }

}
