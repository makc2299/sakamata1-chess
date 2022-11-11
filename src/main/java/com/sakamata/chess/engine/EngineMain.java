package com.sakamata.chess.engine;

import com.sakamata.chess.ChessBoard;

import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

import static com.sakamata.chess.maintenance.ChessConstants.FEN_VALIDATION_REGEX;

public class EngineMain {

    private static ChessBoard board;

    public static boolean pondering = false;

    private static Thread searchThread;
    private static Thread timeThread;
    private static Thread infoThread;

    public static void main(String[] args) {
        Thread.currentThread().setName("sakamata1-main");
        start();
    }

    private static void start() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                parseLine(scanner.nextLine());
            }
        } catch (Throwable t) {
            // TODO logger
            System.out.println(t.getMessage());
//            ErrorLogger.log(cb, t, true);
        }
    }

    public static void parseLine(String line) {
        String[] tokens = line.split(" ");
        switch (tokens[0]) {
            case "uci":
                UCI.sendUci();
                break;
            case "isready":
                System.out.println("readyok");
                break;
            case "ucinewgame":
                // TODO refresh TT
                break;
            case "position":
                inputPosition(tokens);
                break;
            case "go":
                inputGo(tokens);
                break;
            case "print":
                board.printBoard();
                break;
            case "quit":
                System.exit(0);

        }
    }

    /*
    Example UCI commands to init position on chess board

    init start position
    position startpos

    init start position and make the moves on chess board
    position startpos moves e2e4 e7e5

    init position from FEN string
    position fen r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1

    init position from fen string and make moves on chess board
    position fen r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1 moves e2a6 e8g8
    */
    private static void inputPosition(String[] tokens) throws RuntimeException {
        if (tokens[1].equals("startpos")) {
            board = new ChessBoard();
            if (tokens.length > 2) {
                doMoves(Arrays.copyOfRange(tokens, 3, tokens.length));
            }
        } else if (tokens[1].equals("fen")) {
            StringBuilder fen = new StringBuilder();
            int idx = 0;
            for (int i = 2; i < tokens.length; i++) {
                if (tokens[i].equals("moves")) {
                    idx = i;
                    break;
                }
                fen.append(tokens[i]).append(" ");
            }

            if (!fen.toString().matches(FEN_VALIDATION_REGEX)) {
                throw new RuntimeException("FEN string " + fen + " get rejected by engine");
            }

            board = new ChessBoard(fen.toString());

            if (idx != 0) {
                doMoves(Arrays.copyOfRange(tokens, idx + 1, tokens.length));
            }
        }
    }

    private static void doMoves(String[] moveTokens) {
        for (String token : moveTokens) {
            int move = UCI.uciMoveToInt(board, token);
            if (board.isValidMove(move)) {
                board.makeMove(move);
            }
        }
    }

    private static void inputGo(String[] tokens) {

    }
}
