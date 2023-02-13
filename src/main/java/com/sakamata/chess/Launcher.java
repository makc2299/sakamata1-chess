package com.sakamata.chess;

import com.sakamata.chess.engine.UCI;
import com.sakamata.chess.evaluation.SEEIterative;
import com.sakamata.chess.evaluation.SEERecursive;
import com.sakamata.chess.move.MoveEncoder;
import com.sakamata.chess.move.MoveGenerator;
import com.sakamata.chess.move.MoveHolder;
import com.sakamata.chess.search.Search;

import java.util.Arrays;

public class Launcher {

    public static void main(String[] args) {

        // killer fen cause bug
        // "rnbqkb1r/pp1p1pPp/8/2p1pP2/1P1P4/3P3P/P1P1P3/RNBQKBNR w KQkq e6 0 1"

        // testes
        // "1k1r3q/1ppn3p/p4b2/4p3/8/P2N2P1/1PP1R1BP/2K1Q3 w - - "

//        ChessBoard board = new ChessBoard("1k1r3q/1ppn3p/p4b2/4p3/8/P2N2P1/1PP1R1BP/2K1Q3 w - - ");
//        board.printBoard();
//
//        System.out.println(board.zobristKey);
//        Search.setFixedDepth(9);
//        Search search = new Search();
//        System.out.println(UCI.intToUciMove(search.findBestMove(board)));
//        System.out.println("Zobrist key " + board.zobristKey);
//        System.out.println("Nodes : " + search.nodes);
//        System.out.println();

        String[] fens = {
                "1k1r3q/1ppn3p/p4b2/4p3/8/P2N2P1/1PP1R1BP/2K1Q3 w - - ", // SEE position
//                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", // Start position
//                "rnbqkb1r/pp1p1pPp/8/2p1pP2/1P1P4/3P3P/P1P1P3/RNBQKBNR w KQkq e6 0 1", // Killer position
//                "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1 ", // Kiwipete position
//                "r2q1rk1/ppp2ppp/2n1bn2/2b1p3/3pP3/3P1NPP/PPP1NPB1/R1BQ1RK1 b - - 0 9 ", // CMK position
//
//                "1k1r4/1p1n3p/p1p2bq1/4p3/2P1B3/P2N1PP1/1P2R2P/2K1Q3 w - - 0 1",
//                "r1q1k3/p2p1pb1/1n2pnp1/2pPN2r/1pb1P3/1PNQ3p/P1PBBPPP/R3K2R w KQq - 0 1",
//                "r4rk1/1p3p1p/p1pqbnp1/2b1p3/1n1pP1PN/1P1P1P1P/P1P1N1B1/1RBQ1RK1 b - - 0 9",
//                "r1bqkb1r/p1p1p1pp/1pn2p1n/3pP3/6P1/1P1P1N2/P1P2P1P/RNBQKB1R w KQkq d6 0 1",
//                "r3k3/p2pqpb1/bn2pnp1/2pPN2r/1p2P3/1PN2Q1p/P1PBBPPP/R4RK1 w Qq c6 0 1"

//                "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1",
//                "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8  ",
//                "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10",
//                "5rk1/1pp1qp1p/p2p1n2/2b1p1B1/1nB1P1b1/P1NP1NP1/1PP1QP1P/R1R3K1 w - - 0 10"
        };

        for (int i = 0; i < fens.length; i++) {
            System.out.println(i + ") " + fens[i]);
            ChessBoard board = new ChessBoard(fens[i]);
            board.printBoard();

            System.out.println(board.zobristKey);
            Search.setFixedDepth(9);
            Search search = new Search();
            System.out.println(UCI.intToUciMove(search.findBestMove(board)));
            System.out.println("Zobrist key " + board.zobristKey);
            System.out.println("Nodes : " + search.nodes);
            System.out.println();
        }


//        MoveHolder moveHolder = new MoveHolder();
//        MoveGenerator.generateLegalAttacks(board, moveHolder);
//        MoveGenerator.generateLegalMoves(board, moveHolder);
//
//        for (int move : moveHolder.getMoves()) {
//            System.out.print(move + " - " + UCI.intToUciMove(move) + " - " + MoveEncoder.isQuiet(move) + " attacked piece " +
//                    MoveEncoder.getAttackedPieceIndex(move) + " isCapture - " + MoveEncoder.isCapture(move) + "\n");
//        }
//        int move = 43220;
//        System.out.println("SEERecursive: " + SEERecursive.getSeeCaptureScore(board, move));
//        System.out.println("SEEIterative: " + SEEIterative.getSeeCaptureScore(board, move));

//        int boardFeatures = BoardEncoder.createBoardImprint(board.epIndex, board.castlingRights);

//        System.out.println("Start Zobrist : " + board.zobristKey);
//        board.makeMove(6257);
//        System.out.println("After make move " + board.zobristKey);
//        board.printBoard();
//        board.unmakeMove(6257, boardFeatures);
//        System.out.println("After UNMAKE move " + board.zobristKey);
//        board.printBoard();

//        long startZb = board.zobristKey;
//        System.out.println("Start zobrist : " + startZb);
//        for (int move : moveHolder.getMoves()) {
//            System.out.println("\nMake move");
//            System.out.print(move + " - " + UCI.intToUciMove(move) + " - " + MoveEncoder.isQuiet(move) + " attacked piece " + MoveEncoder.getAttackedPieceIndex(move) + "\n");
//
//            board.makeMove(move);
//            board.printBoard();
//            long afterMove = board.zobristKey;
//            System.out.println("After move : " + afterMove);
//
//            board.unmakeMove(move, boardFeatures);
//            board.printBoard();
//            if (startZb != board.zobristKey) {
//                System.exit(727);
//            }
//            System.out.println("After unmake : " + board.zobristKey);
//            System.out.println();
//        }

//        CountDownLatch countDownLatch = new CountDownLatch(2);

    }

}
