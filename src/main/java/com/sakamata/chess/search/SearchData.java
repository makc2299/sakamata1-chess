package com.sakamata.chess.search;

import com.sakamata.chess.engine.EngineConstants;
import com.sakamata.chess.engine.UCI;
import com.sakamata.chess.move.MoveEncoder;

import java.util.Arrays;

import static com.sakamata.chess.maintenance.ChessConstants.*;

public class SearchData {

    public int ply = 0;
    public int previousMove = 0;
    public int currentMove = 0;

    private final int[] pvLength = new int[64];
    private final int[][] pvTable = new int[64][64];

    private final int[] bestMovesByDepth = new int[64];
    private int searchedDepth = 0;

    private final int[] KILLER_MOVE_1 = new int[EngineConstants.MAX_DEPTH];
    private final int[] KILLER_MOVE_2 = new int[EngineConstants.MAX_DEPTH];

    private final int[][][] HISTORY_TABLE = new int[2][7][64];
    private final int[][][] COUNTER_MOVES = new int[2][7][64];

    public void addBestMove(int depth) {
        bestMovesByDepth[depth] = pvTable[0][0];
        searchedDepth = depth;
    }

    public int getBestMove() {
        return bestMovesByDepth[searchedDepth];
    }

    public int getPvValue() {
        return pvTable[0][0];
    }

    public int[] getPVLine() {
        return Arrays.copyOfRange(pvTable[0], 0, pvLength[0]);
    }

    public int getKiller1() {
        return KILLER_MOVE_1[ply];
    }

    public int getKiller2() {
        return KILLER_MOVE_2[ply];
    }

    public void initPVLength() {
        pvLength[ply] = ply;
    }

    public void addPVMove(int move) {
        pvTable[ply][ply] = move;
        for (int nextPly = ply + 1; nextPly < pvLength[ply + 1]; nextPly++) {
            pvTable[ply][nextPly] = pvTable[ply + 1][nextPly];
        }
        pvLength[ply] = pvLength[ply + 1];
    }

    public void addKillerMove(int move) {
        if (KILLER_MOVE_1[ply] != move) {
            KILLER_MOVE_2[ply] = KILLER_MOVE_1[ply];
            KILLER_MOVE_1[ply] = move;
        }
    }

    public void addCounterMove(int sideToMove, int parentMove, int move) {
        COUNTER_MOVES[sideToMove][MoveEncoder.getSourcePieceIndex(parentMove)][MoveEncoder.getToIndex(parentMove)] = move;
    }

    public int getCounterMove(int sideToMove) {
        return COUNTER_MOVES[sideToMove][MoveEncoder.getSourcePieceIndex(previousMove)][MoveEncoder.getToIndex(previousMove)];
    }

    public void updateHistoryTable(int sideToMove, int move, int depth) {
        HISTORY_TABLE[sideToMove][MoveEncoder.getSourcePieceIndex(move)][MoveEncoder.getToIndex(move)] += depth * depth;
    }

    public int getHistoryValue(int sideToMove, int move) {
        return HISTORY_TABLE[sideToMove][MoveEncoder.getSourcePieceIndex(move)][MoveEncoder.getToIndex(move)];
    }

    public void reduceHistoryTable() {
        for (int color = WHITE; color <= BLACK; color++) {
            for (int piece = PAWN; piece <= KING; piece++) {
                for (int square = 0; square < 64; square++) {
                    HISTORY_TABLE[color][piece][square] /= 2;
                }
            }
        }
    }

    public void penalizeHistoryValue(int sideToMove, int move, int fine) {
        HISTORY_TABLE[sideToMove][MoveEncoder.getSourcePieceIndex(move)][MoveEncoder.getToIndex(move)] -= fine;
    }

    public void printPvTable() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (pvTable[i][j] != 0)
                    System.out.printf("%" + 5 + "s", UCI.intToUciMove(pvTable[i][j]));
                else
                    System.out.printf("%" + 5 + "s", pvTable[i][j]);
            }
            System.out.println();
        }
        System.out.println("\n");
    }

}
