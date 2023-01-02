package com.sakamata.chess.move;

import com.sakamata.chess.ChessBoard;
import com.sakamata.chess.evaluation.SEEUtil;
import com.sakamata.chess.search.SearchData;

import java.util.Arrays;

public class MoveHolder {

    public int[] moves = new int[256];
    public int[] quiteMoves = new int[256];
    public int[] attackMoves = new int[256];

    public int count = 0;
    public int countQuite = 0;
    public int countAttack = 0;


    public void addMove(int move) {
        moves[count++] = move;
        quiteMoves[countQuite++] = move;
    }

    public void addAttackMove(int move) {
        moves[count++] = move;
        attackMoves[countAttack++] = move;
    }

    public int[] getMoves() {
        return Arrays.copyOfRange(moves, 0, count);
    }

    public void sort(ChessBoard board, SearchData searchData) {
        int[] moveScores = new int[count];

        for (int i = 0; i < count; i++) {
            moveScores[i] = getMoveScore(board, searchData, moves[i]);
        }
        quickSort(moveScores, 0, count - 1, moves);
    }

    private int getMoveScore(ChessBoard board, SearchData searchData, int move) {
        if (MoveEncoder.isCapture(move)) {
            return SEEUtil.getSeeCaptureScore(board, move) + 1000;
        } else {
            // Killer heuristic
            if (searchData.getKiller1() == move) {
                return 999;
            }
            else if (searchData.getKiller2() == move) {
                return 998;
            }
            else if (searchData.getCounterMove(board.sideToMove) == move) {
                return 997;
            }
            else {
                // Relative History heuristic
                return searchData.getHistoryScore(board.sideToMove, MoveEncoder.getFromToIndex(move));
            }
        }
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
