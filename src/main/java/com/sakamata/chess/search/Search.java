package com.sakamata.chess.search;

import com.sakamata.chess.ChessBoard;
import com.sakamata.chess.engine.UCI;
import com.sakamata.chess.evaluation.Evaluation;
import com.sakamata.chess.move.MoveEncoder;
import com.sakamata.chess.move.MoveGenerator;
import com.sakamata.chess.move.MoveHolder;

import java.util.Arrays;

import static com.sakamata.chess.engine.EngineConstants.MAX_DEPTH;

public class Search {

    public static long nodes;
    public volatile static boolean isRunning = false;

    public static final int MATE_VALUE = 49000;

    public static int currentDepth;
    private static int maxDepth = MAX_DEPTH;


    public static int findBestMove(ChessBoard board) {

        isRunning = true;
        SearchData searchData = new SearchData();

        // iterative deepening
        long controlTme  = System.currentTimeMillis();
        for (int currentDepth = 1; currentDepth <= maxDepth && isRunning; currentDepth++) {
            nodes = 0;
            long time  = System.currentTimeMillis();
//            int value = calculateBestMove(board, searchData, -50_000, 50_000, maxDepth);
            int value = negamax(board, searchData, -50_000, 50_000, currentDepth);

            System.out.printf("info score cp %d depth %d nodes %d time %d pv ", value, currentDepth, nodes,
                    System.currentTimeMillis() - time);
            for (int move : searchData.getPVLine()) {
                System.out.print(UCI.intToUciMove(move) + " ");
            }
            System.out.println();
            searchData.currentMove = 0;

        }
        System.out.println("Time : " + (System.currentTimeMillis() - controlTme));

        return searchData.getBestMove();

//        nodes = 0;
//        isRunning = true;
//        currentDepth = maxDepth;
//        SearchData searchData = new SearchData();
//
//        long time  = System.currentTimeMillis();
////        int value = calculateBestMove(board, searchData, -50_000, 50_000, maxDepth);
//        int value = negamax(board, searchData, -50_000, 50_000, maxDepth);
//
//        System.out.printf("info score cp %d depth %d nodes %d time %d pv ", value, currentDepth, nodes,
//                System.currentTimeMillis() - time);
//        for (int move : searchData.getPVLine()) {
//            System.out.print(UCI.intToUciMove(move) + " ");
//        }
//        System.out.println();
//
//        return searchData.getBestMove();
    }

    public static int quiescence(ChessBoard board, SearchData searchData, int alpha, int beta) {

        nodes++;
        int evaluation = board.checkingPieces != 0 ? -MATE_VALUE + searchData.ply : Evaluation.evaluate(board);

        // fail-hard beta cutoff
        if (evaluation >= beta) {
            return beta;
        }
        // found better move
        alpha = Math.max(alpha, evaluation);

        MoveHolder moveHolder = new MoveHolder();
        MoveGenerator.generateLegalAttacks(board, moveHolder);
        if (board.checkingPieces != 0) {
            MoveGenerator.generateLegalMoves(board, moveHolder);
        }

        moveHolder.sort(board, searchData);
        for (int i = 0; i < moveHolder.count && isRunning; i++) {

            // skip non queen promotion
            if (MoveEncoder.isPromotion(moveHolder.moves[i])) {
                if (MoveEncoder.getMoveType(moveHolder.moves[i]) != MoveEncoder.TYPE_PROMOTION_Q) {
                    continue;
                }
            }

            board.makeMove(moveHolder.moves[i]);
            searchData.ply++;

            int score = -quiescence(board, searchData, -beta, -alpha);

            board.unmakeMove(moveHolder.moves[i]);
            searchData.ply--;

            if (score > alpha) {
                // PV node (position)
                alpha = score;

                // fail-hard beta cutoff
                if (score >= beta) {
                    // node (position) fails high
                    return beta;
                }
            }

        }

        // node (position) fails low
        return alpha;
    }

    public static int calculateBestMove(ChessBoard board, SearchData searchData, int alpha, int beta, int depth) {

        if (!isRunning) {
            return alpha;
        }

        searchData.initPVLength();

        if (depth == 0) {
            return quiescence(board, searchData, alpha, beta);
        }

        nodes++;

        MoveHolder moveHolder = new MoveHolder();
        MoveGenerator.generateLegalAttacks(board, moveHolder);
        MoveGenerator.generateLegalMoves(board, moveHolder);

        // if we don't have any legal moves to make in the current position
        if (moveHolder.count == 0) {
            if (board.checkingPieces != 0) {
                return -MATE_VALUE + searchData.ply;
            } else {
                // draw score
                return 0;
            }
        }

        int parentMove = searchData.currentMove;
        searchData.previousMove = searchData.currentMove;
        moveHolder.sort(board, searchData);
        for (int i = 0; i < moveHolder.count && isRunning; i++) {
            searchData.currentMove = moveHolder.moves[i];
            board.makeMove(moveHolder.moves[i]);
            searchData.ply++;

            int score = -calculateBestMove(board, searchData, -beta, -alpha, depth - 1);

            searchData.previousMove = parentMove;
            board.unmakeMove(moveHolder.moves[i]);
            searchData.ply--;

            if (score >= beta) {
                // Killer move
                if (!MoveEncoder.isCapture(moveHolder.moves[i]) && board.checkingPieces == 0) {
                    searchData.addCounterMove(board.sideToMove, parentMove, moveHolder.moves[i]);
                    searchData.addKillerMove(moveHolder.moves[i]);
                    searchData.updateHistoryTable(board.sideToMove, moveHolder.moves[i], depth);
                }
                return beta;   //  fail hard beta-cutoff
            }
            if (!MoveEncoder.isCapture(moveHolder.moves[i])) {
                searchData.updateButterflyTable(board.sideToMove, moveHolder.moves[i], depth);
            }

            if (score > alpha) {
                alpha = score; // alpha acts like max in MiniMax

                // write PV move
                searchData.addPVMove(moveHolder.moves[i]);
            }

        }
        return alpha;
    }

    public static void setFixedDepth(int depth) {
        maxDepth = depth;
    }

    public static int negamax(ChessBoard board, SearchData searchData, int alpha, int beta, int depth) {

        if (!isRunning) {
            return alpha;
        }

        searchData.initPVLength();

        if (depth == 0) {
            return quiescence(board, searchData, alpha, beta);
        }

        nodes++;

        MoveHolder moveHolder = new MoveHolder();
        MoveGenerator.generateLegalAttacks(board, moveHolder);
        MoveGenerator.generateLegalMoves(board, moveHolder);

        // if we don't have any legal moves to make in the current position
        if (moveHolder.count == 0) {
            if (board.checkingPieces != 0) {
                return -MATE_VALUE + searchData.ply;
            } else {
                // draw score
                return 0;
            }
        }

        int parentMove = searchData.currentMove;
        searchData.previousMove = searchData.currentMove;
        moveHolder.sort(board, searchData);
        for (int i = 0; i < moveHolder.count && isRunning; i++) {
            searchData.currentMove = moveHolder.moves[i];
            board.makeMove(moveHolder.moves[i]);
            searchData.ply++;

            int score = -negamax(board, searchData, -beta, -alpha, depth - 1);

            searchData.previousMove = parentMove;
            board.unmakeMove(moveHolder.moves[i]);
            searchData.ply--;

            if (score >= beta) {
                // Killer move
                if (!MoveEncoder.isCapture(moveHolder.moves[i]) && board.checkingPieces == 0) {
                    searchData.addCounterMove(board.sideToMove, parentMove, moveHolder.moves[i]);
                    searchData.addKillerMove(moveHolder.moves[i]);
                    searchData.updateHistoryTable(board.sideToMove, moveHolder.moves[i], depth);
                }
                return beta;   //  fail hard beta-cutoff
            }
            if (!MoveEncoder.isCapture(moveHolder.moves[i])) {
                searchData.updateButterflyTable(board.sideToMove, moveHolder.moves[i], depth);
            }

            if (score > alpha) {
                alpha = score; // alpha acts like max in MiniMax

                // write PV move
                searchData.addPVMove(moveHolder.moves[i]);
            }

        }
        return alpha;
    }
}
