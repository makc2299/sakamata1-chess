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

        ChessBoard cb = new ChessBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        cb.printBoard();


//        MoveHolder moveHolder = new MoveHolder();
//        MoveGenerator.generateLegalMoves(cb, moveHolder);
//        MoveGenerator.generateLegalAttacks(cb, moveHolder);
//
//        for (int move : moveHolder.moves) {
//            if (move != 0)
//                System.out.println("( " + move + " )  " + (MoveEncoder.isPromotion(move) ? MoveEncoder.getMoveType(move) : "") + " \t"
//                        + Square.getByIndex(MoveEncoder.getFromIndex(move)) + " -> " + Square.getByIndex(MoveEncoder.getToIndex(move)));
//        }

//        cb.printBoard();
//        printPiecesIndexBoard(cb.piecesIndexBoard);
//
//        System.out.println("\nMAKE MOVE   ");
//        cb.makeMove(3415860, 0);
//        cb.printBoard();
//        printPiecesIndexBoard(cb.piecesIndexBoard);
//
//        System.out.println("\nUNMAKE MOVE   ");
//        cb.unmakeMove(3415860);
//        cb.printBoard();
//        printPiecesIndexBoard(cb.piecesIndexBoard);

        // 50348096 - rook move to break castling
        // 50356355 - king move to break castling
        // 51929155 - castling move 1
        // 51929411 - castling move 2

        int depth = 6;
        perft(cb, new MoveHolder(), depth, 0);
        for (int i = 0; i < depth; i++) {
            System.out.println(" Depth : " + (i + 1) + "\t\tNodes : " + cb.moveCounter[i] + "\t\tCaptures : " + cb.captureCounter[i] +
                    "\t\tE.p. : " + cb.enpassCounter[i] + "\t\tCastles : " + cb.castlingCounter[i] + "\t\tPromotions : " + cb.promotionCounter[i]);
            System.out.println("\n");
        }
        cb.printBoard();

////
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

    public static void perft(ChessBoard board, MoveHolder moveHolder, int depth, int ply) {

        if (depth == 0)
            return;

        MoveGenerator.generateLegalMoves(board, moveHolder);
        MoveGenerator.generateLegalAttacks(board, moveHolder);

        for (int i = 0; i < moveHolder.count; i++) {

            int move = moveHolder.moves[i];
            board.makeMove(move, ply);
            perft(board, new MoveHolder(), depth - 1, ply + 1);
            board.unmakeMove(move);
        }

    }

    public static void printPiecesIndexBoard(int[] piecesIndexBoard) {
        for (int i = 63; i >= 0; i-= 8) {
            for (int j = i; j > (i - 8); j--) {
                System.out.print(piecesIndexBoard[j] + " ");
            }
            System.out.println();
        }
    }

}
