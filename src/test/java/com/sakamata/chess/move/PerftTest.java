package com.sakamata.chess.move;

import static org.junit.jupiter.api.Assertions.*;

import com.sakamata.chess.ChessBoard;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PerftTest {

    static class NodesCounter {

        public long[] moveCounter = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        public long[] captureCounter = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        public long[] enPassantCounter = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        public long[] castlingCounter = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        public long[] promotionCounter = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        public void increaseCounter(int move, int ply) {
            moveCounter[ply]++;
            if (MoveEncoder.getAttackedPieceIndex(move) != 0) {
                captureCounter[ply]++;
            }
            if (MoveEncoder.isEPMove(move)) {
                enPassantCounter[ply]++;
            }
            if (MoveEncoder.isCastlingMove(move)) {
                castlingCounter[ply]++;
            }
            if (MoveEncoder.isPromotion(move)) {
                promotionCounter[ply]++;
            }
        }
    }

    private static long perft(ChessBoard board, MoveHolder moveHolder, int depth) {

        if (depth == 0)
            return 1;

        MoveGenerator.generateLegalMoves(board, moveHolder);
        MoveGenerator.generateLegalAttacks(board, moveHolder);

        int counter = 0;
        for (int i = 0; i < moveHolder.count; i++) {

            int move = moveHolder.moves[i];
            board.makeMove(move);
            counter+= perft(board, new MoveHolder(), depth - 1);
            board.unmakeMove(move);
        }
        return counter;
    }

    private static void perftByPly(ChessBoard board, MoveHolder moveHolder, int depth, NodesCounter nodesCounter, int ply) {

        if (depth == 0)
            return ;

        MoveGenerator.generateLegalMoves(board, moveHolder);
        MoveGenerator.generateLegalAttacks(board, moveHolder);

        for (int i = 0; i < moveHolder.count; i++) {

            int move = moveHolder.moves[i];
            board.makeMove(move);
            nodesCounter.increaseCounter(move, ply);
            perftByPly(board, new MoveHolder(), depth - 1, nodesCounter, ply + 1);
            board.unmakeMove(move);
        }
    }

    private static void printNodesByPly(NodesCounter counter, int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.println(" Depth : " + (i + 1) + "\t\tNodes : " + counter.moveCounter[i] + "\t\tCaptures : " +
                    counter.captureCounter[i] + "\t\tE.p. : " + counter.enPassantCounter[i] + "\t\tCastles : " +
                    counter.castlingCounter[i] + "\t\tPromotions : " + counter.promotionCounter[i]);
            System.out.println();
        }
    }

    void test_fen_position(String fen, int depth) {
        ChessBoard board = new ChessBoard(fen);
        NodesCounter counter = new NodesCounter();

        perftByPly(board, new MoveHolder(), depth, counter, 0);

        long[][] correctResults = PerftResults.PERFT_RESULTS.get(fen);

        System.out.println("\nFEN : " + fen);
        printNodesByPly(counter, depth);
        for (int ply = 0; ply < depth & ply < correctResults.length; ply++) {
            assertEquals(counter.moveCounter[ply], correctResults[ply][0]);
            assertEquals(counter.captureCounter[ply], correctResults[ply][1]);
            assertEquals(counter.enPassantCounter[ply], correctResults[ply][2]);
            assertEquals(counter.castlingCounter[ply], correctResults[ply][3]);
            assertEquals(counter.promotionCounter[ply], correctResults[ply][4]);
        }
    }

    @Test
    @DisplayName("Entire tree test")
    void test_for_entire_tree_driver() {
        test_fen_position("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", 6);
        test_fen_position("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ", 5);
        test_fen_position("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ", 7);
        test_fen_position("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1", 6);
    }

    @Test
    @DisplayName("En Passant")
    void en_passant_test_for_specific_ply_driver() {
        assertEquals(perft(new ChessBoard("3k4/3p4/8/K1P4r/8/8/8/8 b - - 0 1"), new MoveHolder(), 7), 20757544);
        assertEquals(perft(new ChessBoard("8/8/4k3/8/2p5/8/B2P2K1/8 w - - 0 1"), new MoveHolder(), 7), 14047573);
        assertEquals(perft(new ChessBoard("8/8/1k6/2b5/2pP4/8/5K2/8 b - d3 0 1"), new MoveHolder(), 7), 21190412);
    }

    @Test
    @DisplayName("Castling")
    void castling_test_for_specific_ply_driver() {
        assertEquals(perft(new ChessBoard("5k2/8/8/8/8/8/8/4K2R w K - 0 1"), new MoveHolder(), 6), 661072);
        assertEquals(perft(new ChessBoard("3k4/8/8/8/8/8/8/R3K3 w Q - 0 1"), new MoveHolder(), 6), 803711);
        assertEquals(perft(new ChessBoard("r3k2r/1b4bq/8/8/8/8/7B/R3K2R w KQkq - 0 1"), new MoveHolder(), 4), 1274206);
        assertEquals(perft(new ChessBoard("r3k2r/8/3Q4/8/8/5q2/8/R3K2R b KQkq - 0 1"), new MoveHolder(), 4), 1720476);

    }

    @Test
    @DisplayName("Promotion")
    void promotion_test_for_specific_ply_driver() {
        assertEquals(perft(new ChessBoard("2K2r2/4P3/8/8/8/8/8/3k4 w - - 0 1"), new MoveHolder(), 7), 60651209);
        assertEquals(perft(new ChessBoard("4k3/1P6/8/8/8/8/K7/8 w - - 0 1"), new MoveHolder(), 7), 3742283);
        assertEquals(perft(new ChessBoard("8/P1k5/K7/8/8/8/8/8 w - - 0 1"), new MoveHolder(), 7), 1555980);
    }

    @Test
    @DisplayName("Stalemate and Checkmate")
    void stalemate_and_checkmate_test_for_specific_ply_driver() {
        assertEquals(perft(new ChessBoard("8/8/1P2K3/8/2n5/1q6/8/5k2 b - - 0 1"), new MoveHolder(), 6), 6334638);
        assertEquals(perft(new ChessBoard("K1k5/8/P7/8/8/8/8/8 w - - 0 1"), new MoveHolder(), 7), 15453);
        assertEquals(perft(new ChessBoard("8/k1P5/8/1K6/8/8/8/8 w - - 0 1"), new MoveHolder(), 8), 2518905);
        assertEquals(perft(new ChessBoard("8/8/2k5/5q2/5n2/8/5K2/8 b - - 0 1"), new MoveHolder(), 6), 3114998);
    }
}
