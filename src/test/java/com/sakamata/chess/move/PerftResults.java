package com.sakamata.chess.move;

import java.util.HashMap;
import java.util.Map;

/**
 * This Class contains the correct values for Nodes, Captures, E.p., Castles, Promotions
 * for each depth for specific FEN string
 * https://www.chessprogramming.org/Perft_Results
 */
public class PerftResults {

    public static final Map<String, long[][]> PERFT_RESULTS = new HashMap<>();
    static {
        PERFT_RESULTS.put("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                new long[][]{   {20L, 0L, 0L, 0L, 0L},
                                {400L, 0L, 0L, 0L, 0L},
                                {8_902L, 34L, 0L, 0L, 0L},
                                {197_281L, 1576L, 0L, 0L, 0L},
                                {4_865_609L, 82_719L, 258L, 0L, 0L},
                                {119_060_324L, 2_812_008L, 5248L, 0L, 0L},
                                {3_195_901_860L, 108_329_926L, 319_617L, 883_453L, 0L},
                                {84_998_978_956L, 3_523_740_106L, 7_187_977L, 23_605_205L, 0L} });

        PERFT_RESULTS.put("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ",
                new long[][]{   {48L, 8L, 0L, 2L, 0L},
                                {2_039L, 351L, 1L, 91L, 0L},
                                {97_862L, 17_102L, 45L, 3_162L, 0L},
                                {4_085_603L, 757_163L, 1_929L, 128_013L, 15_172L},
                                {193_690_690L, 35_043_416L, 73_365L, 4_993_637L, 8_392L},
                                {8_031_647_685L, 1_558_445_089L, 3_577_504L, 184_513_607L, 56_627_920L} });

        PERFT_RESULTS.put("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ",
                new long[][]{   {14L, 1L, 0L, 0L, 0L},
                                {191L, 14L,	0L,	0L,	0L},
                                {2_812L, 209L, 2L, 0L, 0L},
                                {43_238L, 3_348L, 123L, 0L,	0L},
                                {674_624L, 52_051L, 1_165L, 0L, 0L},
                                {11_030_083L, 940_350L, 33_325L, 0L, 7_552L},
                                {178_633_661L, 14_519_036L, 294_874L, 0L, 140_024L},
                                {3_009_794_393L, 267_586_558L, 8_009_239L, 0L, 6_578_076L} });

        PERFT_RESULTS.put("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1",
                new long[][]{   {6L, 0L, 0L, 0L, 0L},
                                {264L, 87L,	0L,	6L,	48L},
                                {9_467L, 1_021L, 4L, 0L, 120L},
                                {422_333L, 131_393L, 0L, 7_795L, 60_032L},
                                {15_833_292L, 2_046_173L, 6_512L, 0L, 329_464L},
                                {706_045_033L, 210_369_132L, 212L, 10_882_006L, 81_102_984L} });
    }
}
