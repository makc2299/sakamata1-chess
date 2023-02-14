package com.sakamata.chess.evaluation;

import com.sakamata.chess.ChessBoard;

import static com.sakamata.chess.evaluation.EvaluationConstants.*;
import static com.sakamata.chess.maintenance.ChessConstants.*;

public class Evaluation {

    public static int evaluate(ChessBoard board) {

        // static evaluation score
        int score = 0;

        int square;
        long peaceBB;
        for (int color = WHITE; color <= BLACK; color++) {
            for (int peace = PAWN; peace <= KING; peace++) {

                peaceBB = board.pieces[color][peace];

                while(peaceBB != 0) {
                    square = Long.numberOfTrailingZeros(peaceBB);

                    if (color == WHITE) {
                        score += MATERIAL[peace];
                        score += PSQ_TABLE[peace][color][square];
                    } else {
                        score -= MATERIAL[peace];
                        score -= PSQ_TABLE[peace][color][square];
                    }

                    peaceBB &= peaceBB - 1;
                }
            }
        }

        return (board.sideToMove == WHITE) ? score : -score;
    }
}
