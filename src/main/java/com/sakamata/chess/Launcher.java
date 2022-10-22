package com.sakamata.chess;

import com.sakamata.chess.maintenance.File;
import com.sakamata.chess.maintenance.Rank;
import com.sakamata.chess.maintenance.Square;
import com.sakamata.chess.maintenance.Util;
import com.sakamata.chess.move.*;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import static com.sakamata.chess.maintenance.ChessConstants.*;

public class Launcher {

    public static void main(String[] args) {

        ChessBoard cb = new ChessBoard("4k3/8/8/8/8/8/8/R3K2R w KQ - 0 1");

        cb.printBoard();
        cb.makeMove(1597763);
        cb.printBoard();
        cb.unmakeMove(1597763);
        cb.printBoard();

        // 16448 - rook move to break castling
        // 24707 - king move to break castling
        // 1597507 - castling move 1
        // 1597763 - castling move 2

//        int depth = 1;
//        perft(cb, new MoveHolder(), depth);
//        System.out.println("depth - " + depth + " moves : " + cb.moveCounter);
//
//        cb.printBoard();
//
//        System.out.println(Arrays.toString(moveHolder.moves));
//        for (int move : moveHolder.moves) {
//            if (move != 0)
//                System.out.println("Source piece index " + ASCII_PIECES[WHITE][MoveEncoder.getSourcePieceIndex(move)] +
//                        " " + MoveEncoder.getFromIndex(move) + " --> " + MoveEncoder.getToIndex(move));
//        }
//        System.out.println(moveHolder.count);

//        Util.printBitboard(MoveGenerator.getAttackingPieces(cb, 28, WHITE));

//        for (Square s : Square.values()) {
//            System.out.println(s);
//            Util.printBitboard(PrecalculatedMoves.getRookAttack(s.ind, a));
//        }

//        int square;
//        long number = 1_000_000_000L;
//
//        long startTime = System.currentTimeMillis();
//
//        long i = 0, n;
//        while (i <= number) {
//            n = Bitboard.RANK_2;
//            i++;
//        }
//
//        System.out.println(" : " + (System.currentTimeMillis() - startTime));
//
//
//        startTime = System.currentTimeMillis();
//
//        long j = 0;
//        while (j <= number) {
//            long n1 = Rank.R2.bitboard;
//            j++;
//        }
//
//        System.out.println(" : " + (System.currentTimeMillis() - startTime));

    }

    public static int getRandomNumberUsingNextInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public static void perft(ChessBoard board, MoveHolder moveHolder, int depth) {

        if (depth == 0)
            return;

        MoveGenerator.generateLegalMoves(board, moveHolder);
        MoveGenerator.generateLegalAttacks(board, moveHolder);

        for (int i = 0; i < moveHolder.count; i++) {

            int move = moveHolder.moves[i];
            board.makeMove(move);
            perft(board, new MoveHolder(), depth - 1);
            board.unmakeMove(move);
        }

    }

}
