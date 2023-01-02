package com.sakamata.chess.engine;

import com.sakamata.chess.ChessBoard;
import com.sakamata.chess.maintenance.Util;
import com.sakamata.chess.move.MoveEncoder;

import static com.sakamata.chess.engine.EngineConstants.AUTHOR_NAME;
import static com.sakamata.chess.engine.EngineConstants.ENGINE_NAME;
import static com.sakamata.chess.maintenance.ChessConstants.*;

public class UCI {

    public static void sendUci() {
        System.out.println("id name " + ENGINE_NAME);
        System.out.println("id author " + AUTHOR_NAME);
        // TODO set options for hashtable
        System.out.println("uciok");
    }

    public static int uciMoveToInt(ChessBoard board, String moveString) {

        if (!moveString.matches("([a-h][1-8]){2}([qrbn])?")) {
            throw new RuntimeException("Received move string does not match a variation of long algebraic notation for moves used in UCI protocol");
        }

        final int fromRank = moveString.charAt(1) - '0';
        final int fromIndex = (fromRank - 1) * 8 + 104 - moveString.charAt(0);

        final int toRank = moveString.charAt(3) - '0';
        final int toIndex = (toRank - 1) * 8 + 104 - moveString.charAt(2);

        final int pieceIndex = board.piecesIndexBoard[fromIndex];
        final int pieceIndexAttacked = board.piecesIndexBoard[toIndex];

        int move = 0;

        if (pieceIndexAttacked == 0) {
            if (pieceIndex == PAWN) {
                if (moveString.length() == 5 && (toRank == 1 || toRank == 8)) {
                    switch (moveString.charAt(4)) {
                        case 'n' -> move = MoveEncoder.createPromotionMove(MoveEncoder.TYPE_PROMOTION_N, fromIndex, toIndex);
                        case 'r' -> move = MoveEncoder.createPromotionMove(MoveEncoder.TYPE_PROMOTION_R, fromIndex, toIndex);
                        case 'b' -> move = MoveEncoder.createPromotionMove(MoveEncoder.TYPE_PROMOTION_B, fromIndex, toIndex);
                        default -> move = MoveEncoder.createPromotionMove(MoveEncoder.TYPE_PROMOTION_Q, fromIndex, toIndex);
                    }
                } else if (toIndex == board.epIndex) {
                    move = MoveEncoder.createEPMove(fromIndex, toIndex);
                } else {
                    move = MoveEncoder.createMove(fromIndex, toIndex, PAWN);
                }
            } else if (pieceIndex == KING) {
                if ((fromIndex - toIndex == 2 || fromIndex - toIndex == -2)) {
                    // castling
                    move = MoveEncoder.createCastlingMove(fromIndex, toIndex, board.castlingRights);
                } else {
                    move = MoveEncoder.createKingMove(fromIndex, toIndex, board.castlingRights);
                }
            } else if (pieceIndex == ROOK) {
                move = MoveEncoder.createRookMove(fromIndex, toIndex, board.castlingRights);
            } else {
                move = MoveEncoder.createMove(fromIndex, toIndex, pieceIndex);
            }
        } else {
            if (pieceIndex == PAWN && (toRank == 1 || toRank == 8)) {
                if (moveString.length() == 5) {
                    switch (moveString.charAt(4)) {
                        case 'n' -> move = MoveEncoder.createPromotionAttack(MoveEncoder.TYPE_PROMOTION_N, fromIndex, toIndex,
                                pieceIndexAttacked, board.castlingRights);
                        case 'r' -> move = MoveEncoder.createPromotionAttack(MoveEncoder.TYPE_PROMOTION_R, fromIndex, toIndex,
                                pieceIndexAttacked, board.castlingRights);
                        case 'b' -> move = MoveEncoder.createPromotionAttack(MoveEncoder.TYPE_PROMOTION_B, fromIndex, toIndex,
                                pieceIndexAttacked, board.castlingRights);
                        default -> move = MoveEncoder.createPromotionAttack(MoveEncoder.TYPE_PROMOTION_Q, fromIndex, toIndex,
                                pieceIndexAttacked, board.castlingRights);
                    }
                }
            } else {
                move = MoveEncoder.createAttackMove(fromIndex, toIndex, pieceIndex, pieceIndexAttacked, board.castlingRights);
            }
        }

        return move;
    }

    public static String intToUciMove(int move) {
        int fromIndex = MoveEncoder.getFromIndex(move);
        int toIndex = MoveEncoder.getToIndex(move);
        String moveString = Util.indexToSquare(fromIndex) + Util.indexToSquare(toIndex);

        if (MoveEncoder.isPromotion(move)) {
            int promotionPeace = MoveEncoder.getMoveType(move);
            if (promotionPeace == MoveEncoder.TYPE_PROMOTION_Q) {
                moveString += "q";
            } else if (promotionPeace == MoveEncoder.TYPE_PROMOTION_N) {
                moveString += "n";
            } else if (promotionPeace == MoveEncoder.TYPE_PROMOTION_R) {
                moveString += "r";
            } else if (promotionPeace == MoveEncoder.TYPE_PROMOTION_B) {
                moveString += "b";
            }
        }

        return moveString;
    }

    public static void sendBestMove(String move) {
        // TODO add ponder to output
        System.out.println("bestmove " + move);
    }
}
