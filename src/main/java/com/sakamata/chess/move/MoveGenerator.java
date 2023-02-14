package com.sakamata.chess.move;

import com.sakamata.chess.Bitboard;
import com.sakamata.chess.CastlingUtil;
import com.sakamata.chess.ChessBoard;
import com.sakamata.chess.engine.EngineConstants;

import static com.sakamata.chess.maintenance.ChessConstants.*;

public class MoveGenerator {

    public static void generateLegalMoves(final ChessBoard board, final MoveHolder moveHolder) {
        if (board.checkingPieces == 0) {
            generateQuiteMoves(board, moveHolder);
        } else if (Long.bitCount(board.checkingPieces) == 1) {
            generateOutOfCheckMoves(board, moveHolder);
        } else {
            addKingMoves(moveHolder, board);
        }
    }

    public static void generateLegalAttacks(final ChessBoard board, final MoveHolder moveHolder) {
        if (board.checkingPieces == 0) {
            generateAttackMoves(board, moveHolder);
        } else if (Long.bitCount(board.checkingPieces) == 1) {
            generateOutOfCheckAttacks(board, moveHolder);
        } else {
            addKingAttacks(moveHolder, board);
        }
    }

    private static void generateQuiteMoves(final ChessBoard board, final MoveHolder moveHolder) {

        final long nonPinned = ~board.pinnedPieces;
        addKnightMoves(moveHolder, board.pieces[board.sideToMove][KNIGHT] & nonPinned, board.emptySpaces);
        addBishopMoves(moveHolder, board.pieces[board.sideToMove][BISHOP] & nonPinned, board.allPieces, board.emptySpaces);
        addRookMoves(moveHolder, board.pieces[board.sideToMove][ROOK] & nonPinned, board.allPieces, board.emptySpaces);
        addQueenMoves(moveHolder, board.pieces[board.sideToMove][QUEEN] & nonPinned, board.allPieces, board.emptySpaces);
        addPawnMoves(moveHolder, board.pieces[board.sideToMove][PAWN] & nonPinned, board, board.emptySpaces);
        addKingMoves(moveHolder, board);

        // pinned pieces
        long piece = board.pieces[board.sideToMove][ALL] & board.pinnedPieces;
        while (piece != 0) {
            switch (board.piecesIndexBoard[Long.numberOfTrailingZeros(piece)]) {
                case PAWN -> addPawnMoves(moveHolder, Long.lowestOneBit(piece), board,
                        board.emptySpaces & PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][board.kingIndex[board.sideToMove]]);
                case BISHOP -> addBishopMoves(moveHolder, Long.lowestOneBit(piece), board.allPieces,
                        board.emptySpaces & PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][board.kingIndex[board.sideToMove]]);
                case ROOK -> addRookMoves(moveHolder, Long.lowestOneBit(piece), board.allPieces,
                        board.emptySpaces & PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][board.kingIndex[board.sideToMove]]);
                case QUEEN -> addQueenMoves(moveHolder, Long.lowestOneBit(piece), board.allPieces,
                        board.emptySpaces & PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][board.kingIndex[board.sideToMove]]);
            }
            piece &= piece - 1;
        }

    }

    private static void generateAttackMoves(final ChessBoard board, final MoveHolder moveHolder) {
        addEpAttacks(moveHolder, board);
        final long nonPinned = ~board.pinnedPieces;
        final long enemies = board.pieces[board.sideToMoveInverse][ALL];
        addPawnAttacksAndPromotions(moveHolder, board.pieces[board.sideToMove][PAWN] & nonPinned, board, enemies, board.emptySpaces);
        addKnightAttacks(moveHolder, board.pieces[board.sideToMove][KNIGHT] & nonPinned, board, enemies);
        addBishopAttacks(moveHolder, board.pieces[board.sideToMove][BISHOP] & nonPinned, board, enemies);
        addRookAttacks(moveHolder, board.pieces[board.sideToMove][ROOK] & nonPinned, board, enemies);
        addQueenAttacks(moveHolder, board.pieces[board.sideToMove][QUEEN] & nonPinned, board, enemies);
        addKingAttacks(moveHolder, board);

        // pinned pieces
        long piece = board.pieces[board.sideToMove][ALL] & board.pinnedPieces;
        while (piece != 0) {
            switch (board.piecesIndexBoard[Long.numberOfTrailingZeros(piece)]) {
                case PAWN -> addPawnAttacksAndPromotions(moveHolder, Long.lowestOneBit(piece), board,
                        enemies & PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][board.kingIndex[board.sideToMove]], 0);
                case BISHOP -> addBishopAttacks(moveHolder, Long.lowestOneBit(piece), board,
                        enemies & PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][board.kingIndex[board.sideToMove]]);
                case ROOK -> addRookAttacks(moveHolder, Long.lowestOneBit(piece), board,
                        enemies & PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][board.kingIndex[board.sideToMove]]);
                case QUEEN -> addQueenAttacks(moveHolder, Long.lowestOneBit(piece), board,
                        enemies & PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][board.kingIndex[board.sideToMove]]);
            }
            piece &= piece - 1;
        }
    }

    private static void generateOutOfCheckMoves(final ChessBoard board, final MoveHolder moveHolder) {
        final long inBetween = SEGMENTS[board.kingIndex[board.sideToMove]][Long.numberOfTrailingZeros(board.checkingPieces)];
        if (inBetween != 0) {
            final long nonPinned = ~board.pinnedPieces;
            addPawnMoves(moveHolder, board.pieces[board.sideToMove][PAWN] & nonPinned, board, inBetween);
            addKnightMoves(moveHolder, board.pieces[board.sideToMove][KNIGHT] & nonPinned, inBetween);
            addBishopMoves(moveHolder, board.pieces[board.sideToMove][BISHOP] & nonPinned, board.allPieces, inBetween);
            addRookMoves(moveHolder, board.pieces[board.sideToMove][ROOK] & nonPinned, board.allPieces, inBetween);
            addQueenMoves(moveHolder, board.pieces[board.sideToMove][QUEEN] & nonPinned, board.allPieces, inBetween);
        }
        addKingMoves(moveHolder, board);
    }

    private static void generateOutOfCheckAttacks(final ChessBoard board, final MoveHolder moveHolder) {
        // attack attacker
        final long nonPinned = ~board.pinnedPieces;
        addEpAttacks(moveHolder, board);
        addPawnAttacksAndPromotions(moveHolder, board.pieces[board.sideToMove][PAWN] & nonPinned, board, board.checkingPieces,
                SEGMENTS[board.kingIndex[board.sideToMove]][Long.numberOfTrailingZeros(board.checkingPieces)]);
        addKnightAttacks(moveHolder, board.pieces[board.sideToMove][KNIGHT] & nonPinned, board, board.checkingPieces);
        addBishopAttacks(moveHolder, board.pieces[board.sideToMove][BISHOP] & nonPinned, board, board.checkingPieces);
        addRookAttacks(moveHolder, board.pieces[board.sideToMove][ROOK] & nonPinned, board, board.checkingPieces);
        addQueenAttacks(moveHolder, board.pieces[board.sideToMove][QUEEN] & nonPinned, board, board.checkingPieces);
        addKingAttacks(moveHolder, board);
    }

    private static void addKnightMoves(final MoveHolder moveHolder, long piece, final long possiblePositions) {
        while (piece != 0) {
            final int fromIndex = Long.numberOfTrailingZeros(piece);
            long moves = PrecalculatedMoves.KNIGHT_MOVES[fromIndex] & possiblePositions;
            while (moves != 0) {
                moveHolder.addMove(MoveEncoder.createMove(fromIndex, Long.numberOfTrailingZeros(moves), KNIGHT));
                moves &= moves - 1;
            }
            piece &= piece - 1;
        }
    }

    private static void addBishopMoves(final MoveHolder moveHolder , long piece, final long allPieces, final long possiblePositions) {
        while (piece != 0) {
            final int fromIndex = Long.numberOfTrailingZeros(piece);
            long moves = PrecalculatedMoves.getBishopAttack(fromIndex, allPieces) & possiblePositions;
            while (moves != 0) {
                moveHolder.addMove(MoveEncoder.createMove(fromIndex, Long.numberOfTrailingZeros(moves), BISHOP));
                moves &= moves - 1;
            }
            piece &= piece - 1;
        }
    }

    private static void addRookMoves(final MoveHolder moveHolder, long piece, final long allPieces, final long possiblePositions) {
        while (piece != 0) {
            final int fromIndex = Long.numberOfTrailingZeros(piece);
            long moves = PrecalculatedMoves.getRookAttack(fromIndex, allPieces) & possiblePositions;
            while (moves != 0) {
                moveHolder.addMove(MoveEncoder.createRookMove(fromIndex, Long.numberOfTrailingZeros(moves)));
                moves &= moves - 1;
            }
            piece &= piece - 1;
        }
    }

    private static void addQueenMoves(final MoveHolder moveHolder, long piece, final long allPieces, final long possiblePositions) {
        while (piece != 0) {
            final int fromIndex = Long.numberOfTrailingZeros(piece);
            long moves = PrecalculatedMoves.getQueenAttack(fromIndex, allPieces) & possiblePositions;
            while (moves != 0) {
                moveHolder.addMove(MoveEncoder.createMove(fromIndex, Long.numberOfTrailingZeros(moves), QUEEN));
                moves &= moves - 1;
            }
            piece &= piece - 1;
        }
    }

    private static void addPawnMoves(final MoveHolder moveHolder, final long pawns, final ChessBoard board, final long possiblePositions) {

        if (pawns == 0) {
            return;
        }

        if (board.sideToMove == WHITE) {
            // 1-move
            long piece = pawns & (possiblePositions >>> 8) & Bitboard.RANK_23456;
            while (piece != 0) {
                moveHolder.addMove(MoveEncoder.createWhitePawnMove(Long.numberOfTrailingZeros(piece)));
                piece &= piece - 1;
            }
            // 2-move
            piece = pawns & (possiblePositions >>> 16) & Bitboard.RANK_2;
            while (piece != 0) {
                if ((board.emptySpaces & (Long.lowestOneBit(piece) << 8)) != 0) {
                    moveHolder.addMove(MoveEncoder.createWhitePawn2Move(Long.numberOfTrailingZeros(piece)));
                }
                piece &= piece - 1;
            }
        } else {
            // 1-move
            long piece = pawns & (possiblePositions << 8) & Bitboard.RANK_34567;
            while (piece != 0) {
                moveHolder.addMove(MoveEncoder.createBlackPawnMove(Long.numberOfTrailingZeros(piece)));
                piece &= piece - 1;
            }
            // 2-move
            piece = pawns & (possiblePositions << 16) & Bitboard.RANK_7;
            while (piece != 0) {
                if ((board.emptySpaces & (Long.lowestOneBit(piece) >>> 8)) != 0) {
                    moveHolder.addMove(MoveEncoder.createBlackPawn2Move(Long.numberOfTrailingZeros(piece)));
                }
                piece &= piece - 1;
            }
        }
    }

    private static void addKingMoves(final MoveHolder moveHolder, final ChessBoard board) {
        final int fromIndex = board.kingIndex[board.sideToMove];
        int toIndex;

        long moves = PrecalculatedMoves.KING_MOVES[fromIndex] & board.emptySpaces;
        while (moves != 0) {
            toIndex = Long.numberOfTrailingZeros(moves);
            if (!MoveUtil.isSquareAttacked(board, toIndex)) {
                moveHolder.addMove(MoveEncoder.createKingMove(fromIndex, toIndex));
            }
            moves &= moves - 1;
        }

        // castling
        if (board.checkingPieces == 0) {
            long castlingIndexes = CastlingUtil.getCastlingIndexes(board);
            while (castlingIndexes != 0) {
                final int castlingIndex = Long.numberOfTrailingZeros(castlingIndexes);
                // no piece in between?
                if (CastlingUtil.isValidCastlingMove(board, fromIndex, castlingIndex)) {
                    moveHolder.addMove(MoveEncoder.createCastlingMove(fromIndex, castlingIndex));
                }
                castlingIndexes &= castlingIndexes - 1;
            }
        }
    }

    private static void addKnightAttacks(final MoveHolder moveHolder, long piece, final ChessBoard board, final long possiblePositions) {
        while (piece != 0) {
            final int fromIndex = Long.numberOfTrailingZeros(piece);
            long moves = PrecalculatedMoves.KNIGHT_MOVES[fromIndex] & possiblePositions;
            while (moves != 0) {
                final int toIndex = Long.numberOfTrailingZeros(moves);
                moveHolder.addAttackMove(MoveEncoder.createAttackMove(fromIndex, toIndex, KNIGHT, board.piecesIndexBoard[toIndex]));
                moves &= moves - 1;
            }
            piece &= piece - 1;
        }
    }

    private static void addBishopAttacks(final MoveHolder moveHolder, long piece, final ChessBoard board, final long possiblePositions) {
        while (piece != 0) {
            final int fromIndex = Long.numberOfTrailingZeros(piece);
            long moves = PrecalculatedMoves.getBishopAttack(fromIndex, board.allPieces) & possiblePositions;
            while (moves != 0) {
                final int toIndex = Long.numberOfTrailingZeros(moves);
                moveHolder.addAttackMove(MoveEncoder.createAttackMove(fromIndex, toIndex, BISHOP, board.piecesIndexBoard[toIndex]));
                moves &= moves - 1;
            }
            piece &= piece - 1;
        }
    }

    private static void addRookAttacks(final MoveHolder moveHolder, long piece, final ChessBoard board, final long possiblePositions) {
        while (piece != 0) {
            final int fromIndex = Long.numberOfTrailingZeros(piece);
            long moves = PrecalculatedMoves.getRookAttack(fromIndex, board.allPieces) & possiblePositions;
            while (moves != 0) {
                final int toIndex = Long.numberOfTrailingZeros(moves);
                moveHolder.addAttackMove(MoveEncoder.createAttackMove(fromIndex, toIndex, ROOK, board.piecesIndexBoard[toIndex]));
                moves &= moves - 1;
            }
            piece &= piece - 1;
        }
    }

    private static void addQueenAttacks(final MoveHolder moveHolder, long piece, final ChessBoard board, final long possiblePositions) {
        while (piece != 0) {
            final int fromIndex = Long.numberOfTrailingZeros(piece);
            long moves = PrecalculatedMoves.getQueenAttack(fromIndex, board.allPieces) & possiblePositions;
            while (moves != 0) {
                final int toIndex = Long.numberOfTrailingZeros(moves);
                moveHolder.addAttackMove(MoveEncoder.createAttackMove(fromIndex, toIndex, QUEEN, board.piecesIndexBoard[toIndex]));
                moves &= moves - 1;
            }
            piece &= piece - 1;
        }
    }

    private static void addKingAttacks(final MoveHolder moveHolder, final ChessBoard board) {
        final int fromIndex = board.kingIndex[board.sideToMove];
        long moves = PrecalculatedMoves.KING_MOVES[fromIndex] & board.pieces[board.sideToMoveInverse][ALL] & ~board.discoveredPieces;
        while (moves != 0) {
            final int toIndex = Long.numberOfTrailingZeros(moves);
            if (!MoveUtil.isSquareAttacked(board, toIndex)) {
                moveHolder.addAttackMove(MoveEncoder.createAttackMove(fromIndex, toIndex, KING, board.piecesIndexBoard[toIndex]));
            }
            moves &= moves - 1;
        }
    }

    private static void addEpAttacks(final MoveHolder moveHolder, final ChessBoard board) {
        if (board.epIndex == 0) {
            return;
        }
        long piece = board.pieces[board.sideToMove][PAWN] & PrecalculatedMoves.PAWN_ATTACKS[board.sideToMoveInverse][board.epIndex];
        while (piece != 0) {
            if (MoveUtil.isLegalEPMove(board, Long.numberOfTrailingZeros(piece))) {
                moveHolder.addAttackMove(MoveEncoder.createEPMove(Long.numberOfTrailingZeros(piece), board.epIndex));
            }
            piece &= piece - 1;
        }
    }

    private static void addPawnAttacksAndPromotions(final MoveHolder moveHolder, final long pawns, final ChessBoard board, final long enemies,
                                                    final long emptySpaces) {
        if (pawns == 0) {
            return;
        }

        if (board.sideToMove == WHITE) {
            // non-promoting
            long moves;
            long piece = pawns & Bitboard.RANK_NON_PROMOTION[WHITE] & Bitboard.getBlackPawnAttacks(enemies);
            while (piece != 0) {
                final int fromIndex = Long.numberOfTrailingZeros(piece);
                moves = PrecalculatedMoves.PAWN_ATTACKS[WHITE][fromIndex] & enemies;
                while (moves != 0) {
                    final int toIndex = Long.numberOfTrailingZeros(moves);
                    moveHolder.addAttackMove(MoveEncoder.createAttackMove(fromIndex, toIndex, PAWN, board.piecesIndexBoard[toIndex]));
                    moves &= moves - 1;
                }
                piece &= piece - 1;
            }

            // promoting
            piece = pawns & Bitboard.RANK_7;
            while (piece != 0) {
                final int fromIndex = Long.numberOfTrailingZeros(piece);

                // promotion move
                if ((Long.lowestOneBit(piece) << 8 & emptySpaces) != 0) {
                    addPromotionMove(moveHolder, fromIndex, fromIndex + 8);
                }

                // promotion attack
                addPromotionAttacks(moveHolder, PrecalculatedMoves.PAWN_ATTACKS[WHITE][fromIndex] & enemies,
                        fromIndex, board.piecesIndexBoard);

                piece &= piece - 1;
            }
        } else {
            // non-promoting
            long moves;
            long piece = pawns & Bitboard.RANK_NON_PROMOTION[BLACK] & Bitboard.getWhitePawnAttacks(enemies);
            while (piece != 0) {
                final int fromIndex = Long.numberOfTrailingZeros(piece);
                moves = PrecalculatedMoves.PAWN_ATTACKS[BLACK][fromIndex] & enemies;
                while (moves != 0) {
                    final int toIndex = Long.numberOfTrailingZeros(moves);
                    moveHolder.addAttackMove(MoveEncoder.createAttackMove(fromIndex, toIndex, PAWN, board.piecesIndexBoard[toIndex]));
                    moves &= moves - 1;
                }
                piece &= piece - 1;
            }

            // promoting
            piece = pawns & Bitboard.RANK_2;
            while (piece != 0) {
                final int fromIndex = Long.numberOfTrailingZeros(piece);

                // promotion move
                if ((Long.lowestOneBit(piece) >>> 8 & emptySpaces) != 0) {
                    addPromotionMove(moveHolder, fromIndex, fromIndex - 8);
                }

                // promotion attacks
                addPromotionAttacks(moveHolder, PrecalculatedMoves.PAWN_ATTACKS[BLACK][fromIndex] & enemies,
                        fromIndex, board.piecesIndexBoard);

                piece &= piece - 1;
            }
        }
    }

    private static void addPromotionMove(final MoveHolder moveHolder, final int fromIndex, final int toIndex) {
        moveHolder.addMove(MoveEncoder.createPromotionMove(MoveEncoder.TYPE_PROMOTION_Q, fromIndex, toIndex));
        moveHolder.addMove(MoveEncoder.createPromotionMove(MoveEncoder.TYPE_PROMOTION_N, fromIndex, toIndex));
        if (EngineConstants.GENERATE_BR_PROMOTIONS) {
            moveHolder.addMove(MoveEncoder.createPromotionMove(MoveEncoder.TYPE_PROMOTION_B, fromIndex, toIndex));
            moveHolder.addMove(MoveEncoder.createPromotionMove(MoveEncoder.TYPE_PROMOTION_R, fromIndex, toIndex));
        }
    }

    private static void addPromotionAttacks(final MoveHolder moveHolder, long moves, final int fromIndex, final int[] pieceIndexes) {
        while (moves != 0) {
            final int toIndex = Long.numberOfTrailingZeros(moves);
            moveHolder.addAttackMove(MoveEncoder.createPromotionAttack(MoveEncoder.TYPE_PROMOTION_Q, fromIndex, toIndex, pieceIndexes[toIndex]));
            moveHolder.addAttackMove(MoveEncoder.createPromotionAttack(MoveEncoder.TYPE_PROMOTION_N, fromIndex, toIndex, pieceIndexes[toIndex]));
            if (EngineConstants.GENERATE_BR_PROMOTIONS) {
                moveHolder.addAttackMove(MoveEncoder.createPromotionAttack(MoveEncoder.TYPE_PROMOTION_B, fromIndex, toIndex, pieceIndexes[toIndex]));
                moveHolder.addAttackMove(MoveEncoder.createPromotionAttack(MoveEncoder.TYPE_PROMOTION_R, fromIndex, toIndex, pieceIndexes[toIndex]));
            }
            moves &= moves - 1;
        }
    }

}
