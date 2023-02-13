package com.sakamata.chess.search;

public class  TimeUtil {

    public static long stopTime;

    private static int movesToGo;
    private static int moveTime;
    private static int increment;
    private static int moveCount;
    private static long totalTimeLeft;
    private static long timeWindowMs;

    private static boolean isTimeControl;

    static {
        reset();
    }

    public static void reset() {
//        startTime = System.currentTimeMillis();
//        stopTime = 0;

        moveTime = -1;
        increment = 0;
        isTimeControl = false;
        movesToGo = 30;
        totalTimeLeft = -1;
        timeWindowMs = 0;
    }

    public static void start() {
        if (moveTime != -1) {
            totalTimeLeft = moveTime;
            movesToGo = 1;
        }

        if (totalTimeLeft != -1) {
            isTimeControl = true;

            timeWindowMs = totalTimeLeft / movesToGo - 50;
            if (increment != 0 && increment <= timeWindowMs / 2) {
                timeWindowMs /= ((double) increment / 1000);
            }
        }
    }

    public static void setTotalTimeLeft(int time) {
        totalTimeLeft = time;
    }

    public static void setIncrement(int inc) {
        increment = inc;
    }

    public static void setMovesToGo(int movesToGo) {
        TimeUtil.movesToGo = movesToGo;
    }

    public static void setMoveTime(int moveTime) {
        TimeUtil.moveTime = moveTime;
    }

    public static boolean isIsTimeControl() {
        return isTimeControl;
    }

    public static long getTimeWindow() {
        return timeWindowMs;
    }
}
