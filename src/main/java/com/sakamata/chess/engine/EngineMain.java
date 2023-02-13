package com.sakamata.chess.engine;

import com.sakamata.chess.ChessBoard;
import com.sakamata.chess.search.Search;
import com.sakamata.chess.search.TimeUtil;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static com.sakamata.chess.maintenance.ChessConstants.*;

public class EngineMain {

    private static ChessBoard board;

    public static boolean pondering = false;
    public volatile static boolean calculating = false;

    private static final Object synchronizedObject = new Object();

    private static final Thread searchThread;
    static {
        searchThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        synchronized (synchronizedObject) {
                            while (!calculating) {
                                synchronizedObject.wait();
                            }
                        }

                        int move = new Search().findBestMove(board);

                        calculating = false;
                        timeThread.interrupt();
                        UCI.sendBestMove(UCI.intToUciMove(move));
                    } catch (Throwable t) {
                        // TODO logger
                    }
                }
            }
        });
        searchThread.setName("sakamata1-search");
        searchThread.setDaemon(true);
    }

    private static final Thread timeThread;
    static {
        timeThread = new Thread(() -> {
            while (true) {
                try {
                    // set thread to wait
                    synchronized (synchronizedObject) {
                        while (!calculating) {
                            synchronizedObject.wait();
                        }
                    }

                    if (TimeUtil.isIsTimeControl()) {
                        Thread.sleep(TimeUtil.getTimeWindow());

                        Search.isRunning = false;
                    }

                } catch (InterruptedException e) {
                    // do nothing
                }
            }
        });
        timeThread.setName("sakamata1-time");
        timeThread.setDaemon(true);
    }

    public static void main(String[] args) {
        Thread.currentThread().setName("sakamata1-main");
        searchThread.start();
        timeThread.start();
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
            case "stop":
                Search.isRunning = false;
                break;
            case "print":
                if (Objects.nonNull(board)) {
                    board.printBoard();
                }
                break;
            case "quit":
                System.exit(0);
            default:
                System.out.println("Unknown command: " + tokens[0]);

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
            } else {
                System.out.println("Move : " + UCI.intToUciMove(move) + " (" + move + ") is not valid");
            }
        }
    }

    /*
    * go - start calculating on the current position set up with the "position" command.
	       There are a number of commands that can follow this command, all will be sent in the same string.
        * searchmoves  ....
            restrict search to this moves only
            Example: After "position startpos" and "go infinite searchmoves e2e4 d2d4"
            the engine should only search the two moves e2e4 and d2d4 in the initial position.
        * ponder - start searching in pondering mode.
        * wtime - white has x msec left on the clock
        * btime - black has x msec left on the clock
        * winc - white increment per move in mseconds if x > 0
        * binc - black increment per move in mseconds if x > 0
        * movestogo - there are x moves to the next time control, this will only be sent if x > 0
        * depth - search x plies only.
        * nodes - search x nodes only,
        * mate - search for a mate in x moves
        * movetime - search exactly x mseconds
        * infinite - search until the "stop" command. Do not exit the search without being told so in this mode!
      Example:
            go depth 6 wtime 180000 btime 100000 binc 1000 winc 1000 movetime 1000 movetogo 40
            go movestogo 30 wtime 3600000 btime 3600000
		    go wtime 40847 btime 48019 winc 0 binc 0 movestogo 20
		    go
		    go infinite
		    go ponder
     */
    private static void inputGo(String[] tokens) {

        TimeUtil.reset();
        Search.setFixedDepth(EngineConstants.MAX_DEPTH);
        pondering = false;

        if (tokens.length != 1) {
            for (int i = 1; i < tokens.length; i++) {
                switch (tokens[i]) {
                    case "infinite":
                        // TODO find out what to do in that case
                        break;
                    case "ponder":
                        pondering = true;
                        break;
                    case "wtime":
                        if (board.sideToMove == WHITE) {
                            TimeUtil.setTotalTimeLeft(Integer.parseInt(tokens[i + 1]));
                        }
                        break;
                    case "btime":
                        if (board.sideToMove == BLACK) {
                            TimeUtil.setTotalTimeLeft(Integer.parseInt(tokens[i + 1]));
                        }
                        break;
                    case "winc":
                        if (board.sideToMove == WHITE) {
                            TimeUtil.setIncrement(Integer.parseInt(tokens[i + 1]));
                        }
                        break;
                    case "binc":
                        if (board.sideToMove == BLACK) {
                            TimeUtil.setIncrement(Integer.parseInt(tokens[i + 1]));
                        }
                        break;
                    case "movestogo":
                        TimeUtil.setMovesToGo(Integer.parseInt(tokens[i + 1]));
                        break;
                    case "movetime":
                        TimeUtil.setMoveTime(Integer.parseInt(tokens[i + 1]));
                        break;
                    case "depth":
                        Search.setFixedDepth(Integer.parseInt(tokens[i + 1]));
                        break;
                }
            }
        }

        TimeUtil.start();

//        int move = new Search().findBestMove(board);
//        UCI.sendBestMove(UCI.intToUciMove(move));

        calculating = true;
        // release all waiting threads
        synchronized (synchronizedObject) {
            synchronizedObject.notifyAll();
        }

    }
}
