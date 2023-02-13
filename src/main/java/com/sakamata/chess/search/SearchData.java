package com.sakamata.chess.search;

import com.sakamata.chess.engine.EngineConstants;
import com.sakamata.chess.move.MoveEncoder;

import java.util.Arrays;

import static com.sakamata.chess.maintenance.ChessConstants.*;

public class SearchData {

    public int ply = 0;
    public int previousMove = 0;
    public int currentMove = 0;

    private final int[] pvLength = new int[64];
    private final int[][] pvTable = new int[64][64];

    public final int[] KILLER_MOVE_1 = new int[EngineConstants.MAX_DEPTH];
    public final int[] KILLER_MOVE_2 = new int[EngineConstants.MAX_DEPTH];

    private final int[][][] HISTORY_TABLE = new int[2][7][64];

    public int getBestMove() {
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
}
