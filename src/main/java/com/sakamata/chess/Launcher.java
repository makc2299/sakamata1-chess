package com.sakamata.chess;

import com.sakamata.chess.engine.EngineMain;
import com.sakamata.chess.engine.UCI;
import com.sakamata.chess.evaluation.Evaluation;
import com.sakamata.chess.evaluation.EvaluationConstants;
import com.sakamata.chess.evaluation.SEEUtil;
import com.sakamata.chess.maintenance.*;
import com.sakamata.chess.move.*;
import com.sakamata.chess.search.Search;
import com.sakamata.chess.search.SearchData;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.sakamata.chess.maintenance.ChessConstants.*;

public class Launcher {

    public static void main(String[] args) {

        // killer fen cause bug
        // "rnbqkb1r/pp1p1pPp/8/2p1pP2/1P1P4/3P3P/P1P1P3/RNBQKBNR w KQkq e6 0 1"

        ChessBoard cb = new ChessBoard( "rnbqkb1r/pp1p1pPp/8/2p1pP2/1P1P4/3P3P/P1P1P3/RNBQKBNR w KQkq e6 0 1");
        cb.printBoard();


//        Util.printPiecesIndexBoard(cb.piecesIndexBoard);
//        System.out.println(Arrays.toString(cb.piecesIndexBoard));
//        Util.printBitboard(cb.allPieces);
//        Util.printBitboard(cb.allPieces & Square.getByIndex(4).bitboard);

        Search.setFixedDepth(8);
        System.out.println(UCI.intToUciMove(Search.findBestMove(cb)));

//        MoveHolder moveHolder = new MoveHolder();
//        MoveGenerator.generateLegalAttacks(cb, moveHolder);
//        MoveGenerator.generateLegalMoves(cb, moveHolder);

//        for (int move : moveHolder.getMoves()) {
//            System.out.print(move + " - " + UCI.intToUciMove(move) + " - " + MoveEncoder.isQuiet(move) + " - " +
//                    Integer.toBinaryString(move));
//            System.out.println();
//        }
//
//        System.out.println("moves : " + Arrays.toString(moveHolder.moves));
//        System.out.println("attack moves : " + Arrays.toString(moveHolder.attackMoves));
//
//        moveHolder.sort(cb);
//
//        System.out.println("AFETR");
//        System.out.println("moves : " + Arrays.toString(moveHolder.moves));
//        System.out.println("attack moves : " + Arrays.toString(moveHolder.attackMoves));
//
//        System.out.println(SEEUtil.getSeeCaptureScore(cb, 4379292));

//        int[] a = new int[] {1000,   0,    -200,   400,    500,    50, 1000, 0};
//        int[] b = new int[] {12343, 34533, 45644, 567567, 123234, 5489, 3433, 788};

//        int[] a = new int[] {1, 2, 3, 4, 5};
//        int[] b = new int[] {4, 5};
//
//        for (int i = a.length - b.length - 1; i >= 0; i--) {
//            a[i + b.length] = a[i];
//        }
//
//        for (int i = 0; i < b.length; i++) {
//            a[i] = b[i];
//        }
//
//        System.out.println(Arrays.toString(a));

//        moveHolder.quickSort(a, 0, a.length - 1, b);
//
//        System.out.println("a : " + Arrays.toString(a));
//        System.out.println("b : " + Arrays.toString(b));

//        System.out.println(SEEUtil.getSeeCaptureScore(cb, 170722));
//
//        Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
//
//        map.put(170722, 1000);
//        map.put(5722, 100);
//        map.put(56722, -200);

//        map.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEach(System.out::println);

//        for (Map.Entry<Integer, Integer> entry: map.entrySet()) {
//            System.out.println(entry.getKey() + " : " + entry.getValue());
//        }
//        System.out.println();
//
//        List<Map.Entry<Integer, Integer>> list = new LinkedList<>(map.entrySet());
//
//        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
//
//        for (Map.Entry<Integer, Integer> entry: list) {
//            System.out.println(entry.getKey() + " : " + entry.getValue());
//        }

//        Search.setFixedDepth(5);
//
//        long time  = System.currentTimeMillis();
//        int move = Search.findBestMove(cb);
//        System.out.println("Time : " + (System.currentTimeMillis() - time)  + " ms");
//
//        UCI.sendBestMove(UCI.intToUciMove(move));

//        int move = UCI.uciMoveToInt(cb,"g6h5");
//        System.out.println(move);
//        System.out.println(UCI.intToUciMove(move));
//        System.out.println(cb.isValidMove(63035433));

//        SearchData searchData = new SearchData();
//
//        long time  = System.currentTimeMillis();
//        int score = Search.negamax(cb, searchData, -50_000, 50_000, 5);
//        System.out.println("Time : " + (System.currentTimeMillis() - time)  + " ms");
//
//        System.out.println("Score : " + score);
//        System.out.println("Move : " + searchData.getBestMove());
//        System.out.println("UCI move : " + UCI.intToUciMove(searchData.getBestMove()));

//        Util.printPiecesIndexBoard(EvaluationConstants.PSQ_TABLE[KING][WHITE], 4);
//        Util.printPiecesIndexBoard(EvaluationConstants.PSQ_TABLE[KING][BLACK], 4);
//        System.out.println("score : " + Evaluation.evaluate(cb));

    }

}
