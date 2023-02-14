package com.sakamata.chess.move;

import com.sakamata.chess.ChessBoard;
import com.sakamata.chess.evaluation.SEEIterative;
import com.sakamata.chess.evaluation.SEERecursive;
import com.sakamata.chess.search.SearchData;

import java.util.Arrays;

import static com.sakamata.chess.evaluation.EvaluationConstants.MVV_LVA;

public class MoveHolder {

    public int[] moves = new int[256];
    public int[] quietMoves = new int[256]; // CLEAN LATER
    public int[] attackMoves = new int[256]; // CLEAN LATER

    public int count = 0;
    public int countQuiet = 0; // CLEAN LATER
    public int countAttack = 0; // CLEAN LATER

    private final int WINNING_CAPTURE_BONUS = 150_000;
    private final int BONUS = 100_000;

    public void addMove(int move) {
        moves[count++] = move;
        quietMoves[countQuiet++] = move; // CLEAN LATER
    }

    public void addAttackMove(int move) {
        moves[count++] = move;
        attackMoves[countAttack++] = move; // CLEAN LATER
    }

    public int[] getMoves() {
        return Arrays.copyOfRange(moves, 0, count);
    }

    public void sort(ChessBoard board, SearchData searchData) {
        int[] moveScores = new int[count];

        int[] captureScores = estimateCaptureMoves(board);
        int[] quietScores = estimateQuietMoves(board, searchData);

        if (countAttack >= 0) System.arraycopy(captureScores, 0, moveScores, 0, countAttack);
        if (countQuiet >= 0) System.arraycopy(quietScores, 0, moveScores, countAttack, countQuiet);

        if (countAttack >= 0) System.arraycopy(attackMoves, 0, moves, 0, countAttack);
        if (countQuiet >= 0) System.arraycopy(quietMoves, 0, moves, countAttack, countQuiet);

        quickSort(moveScores, 0, count - 1, moves);
    }

    public int[] estimateCaptureMoves(ChessBoard board) {
        int[] captureScores = new int[countAttack];

        int score = 0;
        for (int i = 0; i < countAttack; i++) {
            score = SEERecursive.getSeeCaptureScore(board, attackMoves[i]);
            if (score > 0) {
                captureScores[i] = score + WINNING_CAPTURE_BONUS;
            } else if (score == 0) {
                captureScores[i] = MVV_LVA[MoveEncoder.getSourcePieceIndex(attackMoves[i])][MoveEncoder.getAttackedPieceIndex(attackMoves[i])] +
                        BONUS;
            } else {
                captureScores[i] = score;
            }
        }
        return captureScores;
    }

    public int[] estimateQuietMoves(ChessBoard board, SearchData searchData) {
        int[] quietMoveScores = new int[countQuiet];

        int score = 0;
        for (int i = 0; i < countQuiet; i++) {
            if (searchData.getKiller1() == quietMoves[i]) {
                quietMoveScores[i] = BONUS - 1;
            } else if (searchData.getKiller2() == quietMoves[i]) {
                quietMoveScores[i] = BONUS - 2;
            } else if (searchData.getCounterMove(board.sideToMove) == quietMoves[i]) {
                quietMoveScores[i] = BONUS - 3;
            } else {
                score = searchData.getHistoryValue(board.sideToMove, quietMoves[i]);
                // Boundaries for History Table
                if (score > (BONUS - 4)) {
                    searchData.reduceHistoryTable();
                    i = 0;
                    continue;
                }
                quietMoveScores[i] = score;
            }
        }
        return quietMoveScores;
    }

    private void quickSort(int[] arr, int low, int high, int[] relatedArr) {
        if (low < high) {
            int partitionIdx = partition(arr, low, high, relatedArr);
            quickSort(arr, low, partitionIdx - 1, relatedArr);
            quickSort(arr, partitionIdx + 1, high, relatedArr);
        }
    }

    private int partition(int[] arr, int low, int high, int[] relatedArr) {
        int pivot = arr[high];
        int partitionIdx = low;
        for (int i = low; i < high; i++) {
            if (arr[i] >= pivot) {
                swap(arr, i, partitionIdx);
                swap(relatedArr, i, partitionIdx);
                partitionIdx++;
            }
        }
        swap(arr, partitionIdx, high);
        swap(relatedArr, partitionIdx, high);

        return partitionIdx;
    }

    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

}
