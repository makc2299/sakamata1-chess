package com.sakamata.chess.evaluation;

import static com.sakamata.chess.maintenance.ChessConstants.*;

public class EvaluationConstants {

    public static final int[] MATERIAL = {0, 100, 325, 325, 500, 1000, 10000};

    public static final int[] PROMOTION_SCORE = {
            0,
            0,
            MATERIAL[KNIGHT] - MATERIAL[PAWN],
            MATERIAL[BISHOP] - MATERIAL[PAWN],
            MATERIAL[ROOK] - MATERIAL[PAWN],
            MATERIAL[QUEEN] - MATERIAL[PAWN]
    };

    /*
    (Victims) Pawn Knight Bishop   Rook  Queen   King
  (Attackers)
        Pawn   105    205    305    405    505    605
      Knight   104    204    304    404    504    604
      Bishop   103    203    303    403    503    603
        Rook   102    202    302    402    502    602
       Queen   101    201    301    401    501    601
        King   100    200    300    400    500    600
    */
    public static int[][] MVV_LVA = {
            { 0,  0,   0,   0,   0,   0,   0, },
            { 0, 105, 205, 305, 405, 505, 605 },
            { 0, 104, 204, 304, 404, 504, 604 },
            { 0, 103, 203, 303, 403, 503, 603 },
            { 0, 102, 202, 302, 402, 502, 602 },
            { 0, 101, 201, 301, 401, 501, 601 },
            { 0, 100, 200, 300, 400, 500, 600 }
        };

    public static final int[][][] PSQ_TABLE = new int[7][2][64];

    static {
        PSQ_TABLE[PAWN][WHITE] = new int[] {
                0,   0,   0,   0,   0,   0,   0,   0,
                0,   0,   0,  -10, -10,  0,   0,   0,
                0,   0,   0,   5,   5,   0,   0,   0,
                5,   5,   5,   20,  20,  10,  5,   5,
                10,  10,  10,  20,  20,  10,  10,  10,
                20,  20,  30,  30,  30,  20,  20,  20,
                30,  30,  30,  40,  40,  30,  30,  30,
                90,  90,  90,  90,  90,  90,  90,  90
        };

        PSQ_TABLE[KNIGHT][WHITE] = new int[] {
                -5,  -10,  0,   0,   0,   0,  -10, -5,
                -5,   0,   0,   0,   0,   0,   0,  -5,
                -5,   5,   20,  10,  10,  20,  5,  -5,
                -5,   10,  20,  30,  30,  20,  10, -5,
                -5,   10,  20,  30,  30,  20,  10, -5,
                -5,   5,   20,  20,  20,  20,  5,  -5,
                -5,   0,   0,   10,  10,  0,   0,  -5,
                -5,   0,   0,   0,   0 ,  0,   0,  -5
        };

        PSQ_TABLE[BISHOP][WHITE] = new int[] {
                 0,   0,  -10,  0,   0,  -10,  0,   0,
                 0,   30,  0,   0,   0,   0,   30,  0,
                 0,   10,  0,   0,   0,   0,   10,  0,
                 0,   0,   10,  20,  20,  10,  0,   0,
                 0,   0,   10,  20,  20,  10,  0,   0,
                 0,   0,   0,   10,  10,  0,   0,   0,
                 0,   0,   0,   0,   0,   0,   0,   0,
                 0,   0,   0,   0,   0,   0,   0,   0
        };

        PSQ_TABLE[ROOK][WHITE] = new int[] {
                 0,   0,   0,   20,  20,  0,   0,  0,
                 0,   0,   10,  20,  20,  10,  0,  0,
                 0,   0,   10,  20,  20,  10,  0,  0,
                 0,   0,   10,  20,  20,  10,  0,  0,
                 0,   0,   10,  20,  20,  10,  0,  0,
                 0,   0,   10,  20,  20,  10,  0,  0,
                 50,  50,  50,  50,  50,  50,  50, 50,
                 50,  50,  50,  50,  50,  50,  50, 50
        };

        PSQ_TABLE[KING][WHITE] = new int[] {
                 0,   10,  0,  -15,  0,   5,   0,  0,
                 0,   5,   0,  -5,  -5,   5,   5,  0,
                 0,   0,   5,   10,  10,  5,   0,  0,
                 0,   5,   10,  20,  20,  10,  5,  0,
                 0,   5,   10,  20,  20,  10,  5,  0,
                 0,   5,   5,   10,  10,  5,   5,  0,
                 0,   0,   5,   5,   5,   5,   0,  0,
                 0,   0,   0,   0,   0,   0,   0,  0
        };

        for (int peace = PAWN; peace <= KING; peace++) {
            for (int i = 0; i < 8; i++) {
                for (int j = 56 - 8 * i; j < 56 - 8 * i + 8; j++) {
                    PSQ_TABLE[peace][BLACK][j] = PSQ_TABLE[peace][WHITE][j % 8 + i * 8];
                }
            }
        }
    }
}
