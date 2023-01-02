package com.sakamata.chess;

import com.sakamata.chess.maintenance.ChessConstants;
import com.sakamata.chess.move.MoveEncoder;
import com.sakamata.chess.move.MoveUtil;
import com.sakamata.chess.move.PrecalculatedMoves;
import com.sakamata.chess.maintenance.Square;

import java.util.Arrays;

import static com.sakamata.chess.maintenance.ChessConstants.*;

public class ChessBoard {

    public final long[][] pieces = new long[2][7];
    public final int[] kingIndex = new int[2];
    public long allPieces;
    public long emptySpaces;
    public final int[] piecesIndexBoard = new int[64];

    public int sideToMove, sideToMoveInverse;
    public int castlingRights;
    public int epIndex;
    public int halfMoveClock;
    public int fullMoveCounter;

    public int phase;
    public long checkingPieces, pinnedPieces, discoveredPieces;

    public ChessBoard() {
        applyFen(ChessConstants.FEN_START);
    }

    public ChessBoard(String fen) {
        applyFen(fen);
    }

    public void makeMove(int move) {

        final int fromIndex = MoveEncoder.getFromIndex(move);
        int toIndex = MoveEncoder.getToIndex(move);
        final int sourcePieceIndex = MoveEncoder.getSourcePieceIndex(move);
        final int attackedPieceIndex = MoveEncoder.getAttackedPieceIndex(move);

        long toMask = 1L << toIndex;
        final long fromToMask = (1L << fromIndex) ^ toMask;

        if (epIndex != 0) {
            epIndex = 0;
        }

        pieces[sideToMove][ALL] ^= fromToMask;
        pieces[sideToMove][sourcePieceIndex] ^= fromToMask;
        piecesIndexBoard[fromIndex] = EMPTY;
        piecesIndexBoard[toIndex] = sourcePieceIndex;

        switch (sourcePieceIndex) {
            case PAWN:
                if (MoveEncoder.isPromotion(move)) {
                    pieces[sideToMove][PAWN] ^= toMask;
                    pieces[sideToMove][MoveEncoder.getMoveType(move)] |= toMask;
                    piecesIndexBoard[toIndex] = MoveEncoder.getMoveType(move);
                } else {
                    // 2-move
                    if (SEGMENTS[fromIndex][toIndex] != 0) {
                        if ((PrecalculatedMoves.PAWN_ATTACKS[sideToMove][Long.numberOfTrailingZeros(SEGMENTS[fromIndex][toIndex])]
                                & pieces[sideToMoveInverse][PAWN]) != 0) {
                            epIndex = Long.numberOfTrailingZeros(SEGMENTS[fromIndex][toIndex]);
                        }
                    }
                }
                break;

            case ROOK:
                if (castlingRights != 0) {
                    castlingRights = CastlingUtil.getCastlingRightsAfterRookMovedOrAttacked(castlingRights, fromIndex);
                }
                break;

            case KING:
                kingIndex[sideToMove] = toIndex;
                if (castlingRights != 0) {
                    if (MoveEncoder.isCastlingMove(move)) {
                        CastlingUtil.castleRookUpdate(this, toIndex);
                    }
                    castlingRights = CastlingUtil.getCastlingRightsAfterKingMoved(castlingRights, fromIndex);
                }
        }

        // piece hit?
        switch (attackedPieceIndex) {
            case EMPTY:
                break;
            case PAWN:
                if (MoveEncoder.isEPMove(move)) {
                    toIndex += EN_PASSANT_SHIFT[sideToMoveInverse];
                    toMask = Square.getByIndex(toIndex).bitboard;
                    piecesIndexBoard[toIndex] = EMPTY;
                }
                pieces[sideToMoveInverse][ALL] ^= toMask;
                pieces[sideToMoveInverse][PAWN] ^= toMask;
                break;
            case ROOK:
                if (castlingRights != 0) {
                    castlingRights = CastlingUtil.getCastlingRightsAfterRookMovedOrAttacked(castlingRights, toIndex);
                }
                // fall-through
            default:
                pieces[sideToMoveInverse][ALL] ^= toMask;
                pieces[sideToMoveInverse][attackedPieceIndex] ^= toMask;
        }

        allPieces = pieces[sideToMove][ALL] | pieces[sideToMoveInverse][ALL];
        emptySpaces = ~allPieces;
        changeSideToMove();
        setCheckingPinnedDiscoveredPieces();
    }

    public void unmakeMove(final int move) {

        final int fromIndex = MoveEncoder.getFromIndex(move);
        int toIndex = MoveEncoder.getToIndex(move);
        final int sourcePieceIndex = MoveEncoder.getSourcePieceIndex(move);
        final int attackedPieceIndex = MoveEncoder.getAttackedPieceIndex(move);

        long toMask = 1L << toIndex;
        final long fromToMask = (1L << fromIndex) ^ toMask;

        // undo move
        pieces[sideToMoveInverse][ALL] ^= fromToMask;
        pieces[sideToMoveInverse][sourcePieceIndex] ^= fromToMask;
        piecesIndexBoard[fromIndex] = sourcePieceIndex;

        switch (sourcePieceIndex) {
            case EMPTY:
                // not necessary but provides a table-index
                break;
            case PAWN:
                if (MoveEncoder.isPromotion(move)) {
                    pieces[sideToMoveInverse][PAWN] ^= toMask;
                    pieces[sideToMoveInverse][MoveEncoder.getMoveType(move)] ^= toMask;
                } else {
//                    pawnZobristKey ^= Zobrist.piece[colorToMoveInverse][PAWN][toIndex];
                }
                break;
            case ROOK:
                if (MoveEncoder.getCastling(move) != 0) {
                    castlingRights = MoveEncoder.getCastling(move);
                }
                break;
            case KING:
                if (MoveEncoder.isCastlingMove(move)) {
                    CastlingUtil.uncastleRookUpdate(this, toIndex);
                    castlingRights = MoveEncoder.getCastling(move);
                } else if (MoveEncoder.getCastling(move) != 0) {
                    castlingRights = MoveEncoder.getCastling(move);
                }
                kingIndex[sideToMoveInverse] = fromIndex;
        }

        // undo hit
        switch (attackedPieceIndex) {
            case EMPTY:
                break;
            case PAWN:
                if (MoveEncoder.isEPMove(move)) {
                    piecesIndexBoard[toIndex] = EMPTY;
                    epIndex = toIndex;
                    toIndex += EN_PASSANT_SHIFT[sideToMove];
                    toMask = Square.getByIndex(toIndex).bitboard;
                }
            case ROOK:
                if (MoveEncoder.getCastling(move) != 0) {
                    castlingRights = MoveEncoder.getCastling(move);
                }
                // fall-through
            default:
                pieces[sideToMove][ALL] |= toMask;
                pieces[sideToMove][attackedPieceIndex] |= toMask;
        }

        piecesIndexBoard[toIndex] = attackedPieceIndex;
        allPieces = pieces[sideToMove][ALL] | pieces[sideToMoveInverse][ALL];
        emptySpaces = ~allPieces;
        changeSideToMove();
        setCheckingPinnedDiscoveredPieces();
    }

    public void init() {

        allPieces = pieces[WHITE][ALL] | pieces[BLACK][ALL];
        emptySpaces = ~allPieces;

        kingIndex[WHITE] = Long.numberOfTrailingZeros(pieces[WHITE][KING]);
        kingIndex[BLACK] = Long.numberOfTrailingZeros(pieces[BLACK][KING]);

        sideToMoveInverse = sideToMove ^ 1;

        Arrays.fill(piecesIndexBoard, EMPTY);
        long piece;
        for (int color = WHITE; color <= BLACK; color++) {
            for (int pieceIndex = PAWN; pieceIndex <= KING; pieceIndex++) {
                piece = pieces[color][pieceIndex];
                while (piece != 0) {
                    piecesIndexBoard[Long.numberOfTrailingZeros(piece)] = pieceIndex;
                    piece &= piece - 1;
                }
            }
        }

        setCheckingPinnedDiscoveredPieces();
    }

    public void changeSideToMove() {
        sideToMove = sideToMoveInverse;
        sideToMoveInverse ^= 1;
    }

    public void setCheckingPinnedDiscoveredPieces() {
        pinnedPieces = 0;
        discoveredPieces = 0;
        checkingPieces = pieces[sideToMoveInverse][KNIGHT] & PrecalculatedMoves.KNIGHT_MOVES[kingIndex[sideToMove]]
                | pieces[sideToMoveInverse][PAWN] & PrecalculatedMoves.PAWN_ATTACKS[sideToMove][kingIndex[sideToMove]];

        int enemyColor;
        long pinnedPiece;
        for (int kingColor = WHITE; kingColor <= BLACK; kingColor++) {

            enemyColor = kingColor ^ 1;

            long slidingCheckingPiece = (pieces[enemyColor][BISHOP] | pieces[enemyColor][QUEEN]) & PrecalculatedMoves.getBishopAttack(kingIndex[kingColor], 0L)
                    | (pieces[enemyColor][ROOK] | pieces[enemyColor][QUEEN]) & PrecalculatedMoves.getRookAttack(kingIndex[kingColor], 0L);
            while (slidingCheckingPiece != 0) {
                pinnedPiece = SEGMENTS[kingIndex[kingColor]][Long.numberOfTrailingZeros(slidingCheckingPiece)] & allPieces;
                if (pinnedPiece == 0) {
                    checkingPieces |= Long.lowestOneBit(slidingCheckingPiece);
                } else if (Long.bitCount(pinnedPiece) == 1) {
                    pinnedPieces |= pinnedPiece & pieces[kingColor][ALL];
                    discoveredPieces |= pinnedPiece & pieces[enemyColor][ALL];
                }
                slidingCheckingPiece &= slidingCheckingPiece - 1;
            }
        }
    }

    public boolean isValidMove(final int move) {

        // check if from index square not empty
        final int fromIndex = MoveEncoder.getFromIndex(move);
        final long fromSquare = Square.getByIndex(fromIndex).bitboard;
        if ((pieces[sideToMove][MoveEncoder.getSourcePieceIndex(move)] & fromSquare) == 0) {
            return false;
        }

        // check piece at to square
        final int toIndex = MoveEncoder.getToIndex(move);
        final long toSquare = Square.getByIndex(toIndex).bitboard;
        final int attackedPieceIndex = MoveEncoder.getAttackedPieceIndex(move);
        if (attackedPieceIndex == 0) {
            if (piecesIndexBoard[toIndex] != EMPTY) {
                return false;
            }
        } else {
            if ((pieces[sideToMoveInverse][attackedPieceIndex] & toSquare) == 0 && !MoveEncoder.isEPMove(move)) {
                return false;
            }
        }

        // check if move is possible
        switch (MoveEncoder.getSourcePieceIndex(move)) {
            case PAWN:
                if (MoveEncoder.isEPMove(move)) {
                    if (toIndex != epIndex) {
                        return false;
                    }
                    return MoveUtil.isLegalEPMove(this, fromIndex);
                }

                if (sideToMove == WHITE) {
                    if (fromIndex > toIndex) {
                        return false;
                    }
                    // 2-move
                    if (toIndex - fromIndex == 16 && (allPieces & Square.getByIndex(fromIndex + 8).bitboard) != 0) {
                        return false;
                    }
                } else {
                    if (fromIndex < toIndex) {
                        return false;
                    }
                    // 2-move
                    if (fromIndex - toIndex == 16 && (allPieces & Square.getByIndex(fromIndex - 8).bitboard) != 0) {
                        return false;
                    }
                }
                break;
            case BISHOP:
                // fall-through
            case ROOK:
                // fall-through
            case QUEEN:
                if ((SEGMENTS[fromIndex][toIndex] & allPieces) != 0) {
                    return false;
                }
                break;
            case KING:
                if (MoveEncoder.isCastlingMove(move)) {
                    long castlingIndexes = CastlingUtil.getCastlingIndexes(this);
                    while (castlingIndexes != 0) {
                        if (toIndex == Long.numberOfTrailingZeros(castlingIndexes)) {
                            return CastlingUtil.isValidCastlingMove(this, fromIndex, toIndex);
                        }
                        castlingIndexes &= castlingIndexes - 1;
                    }
                    return false;
                }
                return !MoveUtil.isSquareAttacked(this, MoveEncoder.getToIndex(move));
        }

        if ((fromSquare & pinnedPieces) != 0) {
            if ((PINNED_MOVEMENT[fromIndex][kingIndex[sideToMove]] & toSquare) == 0) {
                return false;
            }
        }

        if (checkingPieces != 0) {
            if (attackedPieceIndex == 0) {
                return !MoveUtil.isInCheck(kingIndex[sideToMove], sideToMove, pieces[sideToMoveInverse],
                        allPieces ^ Square.getByIndex(MoveEncoder.getFromIndex(move)).bitboard ^
                                Square.getByIndex(MoveEncoder.getToIndex(move)).bitboard);
            } else {
                if (Long.bitCount(checkingPieces) == 2) {
                    return false;
                }
                return (toSquare & checkingPieces) != 0;
            }
        }

        return true;
    }

    public void printBoard() {

        char[] asciiBoard = new char[64];

        for (int color = WHITE; color <= BLACK; color++) {
            for (int piece = PAWN; piece <= KING; piece++) {
                long bitboard = pieces[color][piece];

                while (bitboard != 0) {
                    int index = Bitboard.indexOfMSBit(bitboard);
                    bitboard ^= Bitboard.toBit(index);

                    asciiBoard[63 - index] = ASCII_PIECES[color][piece];
                }
            }
        }

        for (int i = 0; i < 8; i++) {
            System.out.print(8 - i + "  ");
            for (int j = i * 8; j < (i * 8) + 8; j++) {
                System.out.print(Character.isAlphabetic(asciiBoard[j]) ? asciiBoard[j] + " " : ". ");
            }
            if (i == 0) System.out.printf("    Side: %c", sideToMove == WHITE ? 'w' : 'b');
            if (i == 1) System.out.printf("    Castling: %c%c%c%c", (castlingRights & WK) != 0 ? 'K' : '-',
                                                                    (castlingRights & WQ) != 0 ? 'Q' : '-',
                                                                    (castlingRights & BK) != 0 ? 'k' : '-',
                                                                    (castlingRights & BQ) != 0 ? 'q' : '-');
            if (i == 2) System.out.printf("    En passant: %s", (epIndex != EMPTY) ? Square.getByIndex(epIndex) : '-');
            if (i == 3) System.out.printf("    50 Move clock: %d", halfMoveClock);
            if (i == 4) System.out.printf("    Full move counter: %d", fullMoveCounter);
            System.out.println();
        }
        System.out.println("   a b c d e f g h\n");

    }

    // FEN string example : rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
    public void applyFen(String fen) {

        String[] fenArray = fen.split(" ");
        setPieces(fenArray[0]);

        sideToMove = fenArray[1].equals("w") ? WHITE : BLACK;

        castlingRights = 0;
        halfMoveClock = 0;
        fullMoveCounter = 0;

        if (fenArray[2].contains("K")) {
            castlingRights |= 8;
        }
        if (fenArray[2].contains("Q")) {
            castlingRights |= 4;
        }
        if (fenArray[2].contains("k")) {
            castlingRights |= 2;
        }
        if (fenArray[2].contains("q")) {
            castlingRights |= 1;
        }

        if (fenArray[3].equals("-") || fenArray[3].equals("–")) {
            epIndex = EMPTY;
        } else {
            epIndex = 104 - fenArray[3].charAt(0) + 8 * (Integer.parseInt(fenArray[3].substring(1)) - 1);
        }

        if (fenArray.length > 4) {
            halfMoveClock = Integer.parseInt(fenArray[4]);
            fullMoveCounter = Integer.parseInt(fenArray[5]);
        }

        init();
    }

    public String getFen() {
        StringBuilder sb = new StringBuilder();
        for (int i = 63; i >= 0; i--) {
            if ((pieces[WHITE][ALL] & Square.getByIndex(i).bitboard) != 0) {
                sb.append(ASCII_PIECES[WHITE][piecesIndexBoard[i]]);
            } else {
                sb.append(ASCII_PIECES[BLACK][piecesIndexBoard[i]]);
            }
            if (i % 8 == 0 && i != 0) {
                sb.append("/");
            }
        }

        // color to move
        sb.append(" ").append(sideToMove == WHITE ? "w" : "b").append(" ");

        // castling rights
        if (castlingRights == 0) {
            sb.append("-");
        } else {
            if ((castlingRights & 8) != 0) { // 1000
                sb.append("K");
            }
            if ((castlingRights & 4) != 0) { // 0100
                sb.append("Q");
            }
            if ((castlingRights & 2) != 0) { // 0010
                sb.append("k");
            }
            if ((castlingRights & 1) != 0) { // 0001
                sb.append("q");
            }
        }

        // en passant
        sb.append(" ");
        if (epIndex == 0) {
            sb.append("-");
        } else {
            sb.append(Character.toString(104 - epIndex % 8)).append(epIndex / 8 + 1);
        }

        String fen = sb.toString();
        fen = fen.replaceAll("11111111", "8");
        fen = fen.replaceAll("1111111", "7");
        fen = fen.replaceAll("111111", "6");
        fen = fen.replaceAll("11111", "5");
        fen = fen.replaceAll("1111", "4");
        fen = fen.replaceAll("111", "3");
        fen = fen.replaceAll("11", "2");

        return fen;
    }

    private void setPieces(String fenPieces) {

        for (int color = 0; color < 2; color++) {
            for (int piece = ALL; piece <= KING; piece++) {
                pieces[color][piece] = 0L;
            }
        }
        allPieces = 0;

        int position = 63;
        char character;
        for (int i = 0; i < fenPieces.length(); i++) {

            character = fenPieces.charAt(i);

            switch (character) {
                case '/':
                    continue;
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                    position -= Character.digit(character, 10);
                    break;
                case 'P':
                    pieces[WHITE][PAWN] |= Square.getByIndex(position).bitboard;
                    pieces[WHITE][ALL] |= Square.getByIndex(position--).bitboard;
                    break;
                case 'N':
                    pieces[WHITE][KNIGHT] |= Square.getByIndex(position).bitboard;
                    pieces[WHITE][ALL] |= Square.getByIndex(position--).bitboard;
                    break;
                case 'B':
                    pieces[WHITE][BISHOP] |= Square.getByIndex(position).bitboard;
                    pieces[WHITE][ALL] |= Square.getByIndex(position--).bitboard;
                    break;
                case 'R':
                    pieces[WHITE][ROOK] |= Square.getByIndex(position).bitboard;
                    pieces[WHITE][ALL] |= Square.getByIndex(position--).bitboard;
                    break;
                case 'Q':
                    pieces[WHITE][QUEEN] |= Square.getByIndex(position).bitboard;
                    pieces[WHITE][ALL] |= Square.getByIndex(position--).bitboard;
                    break;
                case 'K':
                    pieces[WHITE][KING] |= Square.getByIndex(position).bitboard;
                    pieces[WHITE][ALL] |= Square.getByIndex(position--).bitboard;
                    break;
                case 'p':
                    pieces[BLACK][PAWN] |= Square.getByIndex(position).bitboard;
                    pieces[BLACK][ALL] |= Square.getByIndex(position--).bitboard;
                    break;
                case 'n':
                    pieces[BLACK][KNIGHT] |= Square.getByIndex(position).bitboard;
                    pieces[BLACK][ALL] |= Square.getByIndex(position--).bitboard;
                    break;
                case 'b':
                    pieces[BLACK][BISHOP] |= Square.getByIndex(position).bitboard;
                    pieces[BLACK][ALL] |= Square.getByIndex(position--).bitboard;
                    break;
                case 'r':
                    pieces[BLACK][ROOK] |= Square.getByIndex(position).bitboard;
                    pieces[BLACK][ALL] |= Square.getByIndex(position--).bitboard;
                    break;
                case 'q':
                    pieces[BLACK][QUEEN] |= Square.getByIndex(position).bitboard;
                    pieces[BLACK][ALL] |= Square.getByIndex(position--).bitboard;
                    break;
                case 'k':
                    pieces[BLACK][KING] |= Square.getByIndex(position).bitboard;
                    pieces[BLACK][ALL] |= Square.getByIndex(position--).bitboard;
                    break;
            }
        }
    }

}
