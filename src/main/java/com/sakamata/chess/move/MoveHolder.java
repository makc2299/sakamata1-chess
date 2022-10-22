package com.sakamata.chess.move;

public class MoveHolder {

    public int[] moves = new int[256];
    public int count = 0;

    public void addMove(int move) {
        moves[count++] = move;
    }
}
