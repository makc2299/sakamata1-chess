package com.sakamata.chess.maintenance;

public class Util {

    public static void printBitboard(long bitboard) {
        String fullBinaryStr = "0".repeat(Long.numberOfLeadingZeros(bitboard)) + Long.toBinaryString(bitboard);

        for (int i = 0; i < 8; i++) {
            System.out.print(8 - i + "  ");
            for (int j = i * 8; j < (i * 8) + 8; j++) {
                System.out.print(fullBinaryStr.charAt(j) + " ");
            }
            System.out.println();
        }
        System.out.println("   a b c d e f g h");
        System.out.println("\n   Binary : " + Long.toBinaryString(bitboard));
        System.out.println("   Decimal : " + bitboard + "\n");
    }

    public static void printPiecesIndexBoard(int[] piecesIndex) {
        for (int i = 63; i >= 0; i-= 8) {
            System.out.print(i / 8 + 1 + "  ");
            for (int j = i; j > (i - 8); j--) {
                System.out.print(piecesIndex[j] + " ");
            }
            System.out.println();
        }
        System.out.println("   a b c d e f g h\n");
    }
}
