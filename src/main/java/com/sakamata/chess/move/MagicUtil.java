package com.sakamata.chess.move;

import com.sakamata.chess.Bitboard;

public class MagicUtil {

    public static final int[] bishopOccupancyBits =
            {
                    6, 5, 5, 5, 5, 5, 5, 6,
                    5, 5, 5, 5, 5, 5, 5, 5,
                    5, 5, 7, 7, 7, 7, 5, 5,
                    5, 5, 7, 9, 9, 7, 5, 5,
                    5, 5, 7, 9, 9, 7, 5, 5,
                    5, 5, 7, 7, 7, 7, 5, 5,
                    5, 5, 5, 5, 5, 5, 5, 5,
                    6, 5, 5, 5, 5, 5, 5, 6
            };

    public static final int[] rookOccupancyBits =
            {
                    12, 11, 11, 11, 11, 11, 11, 12,
                    11, 10, 10, 10, 10, 10, 10, 11,
                    11, 10, 10, 10, 10, 10, 10, 11,
                    11, 10, 10, 10, 10, 10, 10, 11,
                    11, 10, 10, 10, 10, 10, 10, 11,
                    11, 10, 10, 10, 10, 10, 10, 11,
                    11, 10, 10, 10, 10, 10, 10, 11,
                    12, 11, 11, 11, 11, 11, 11, 12
            };

    public static final long[] rookMagicNumbers = {
            0x8a80104000800020L, 0x140002000100040L, 0x2801880a0017001L, 0x100081001000420L, 0x200020010080420L,
            0x3001c0002010008L, 0x8480008002000100L, 0x2080088004402900L, 0x800098204000L, 0x2024401000200040L,
            0x100802000801000L, 0x120800800801000L, 0x208808088000400L, 0x2802200800400L, 0x2200800100020080L,
            0x801000060821100L, 0x80044006422000L, 0x100808020004000L, 0x12108a0010204200L, 0x140848010000802L,
            0x481828014002800L, 0x8094004002004100L, 0x4010040010010802L, 0x20008806104L, 0x100400080208000L,
            0x2040002120081000L, 0x21200680100081L, 0x20100080080080L, 0x2000a00200410L, 0x20080800400L,
            0x80088400100102L, 0x80004600042881L, 0x4040008040800020L, 0x440003000200801L, 0x4200011004500L,
            0x188020010100100L, 0x14800401802800L, 0x2080040080800200L, 0x124080204001001L, 0x200046502000484L,
            0x480400080088020L, 0x1000422010034000L, 0x30200100110040L, 0x100021010009L, 0x2002080100110004L,
            0x202008004008002L, 0x20020004010100L, 0x2048440040820001L, 0x101002200408200L, 0x40802000401080L,
            0x4008142004410100L, 0x2060820c0120200L, 0x1001004080100L, 0x20c020080040080L, 0x2935610830022400L,
            0x44440041009200L, 0x280001040802101L, 0x2100190040002085L, 0x80c0084100102001L, 0x4024081001000421L,
            0x20030a0244872L, 0x12001008414402L, 0x2006104900a0804L, 0x1004081002402L
    };

    public static final long[] bishopMagicNumbers = {
            0x40040844404084L, 0x2004208a004208L, 0x10190041080202L, 0x108060845042010L, 0x581104180800210L,
            0x2112080446200010L, 0x1080820820060210L, 0x3c0808410220200L, 0x4050404440404L, 0x21001420088L,
            0x24d0080801082102L, 0x1020a0a020400L, 0x40308200402L, 0x4011002100800L, 0x401484104104005L,
            0x801010402020200L, 0x400210c3880100L, 0x404022024108200L, 0x810018200204102L, 0x4002801a02003L,
            0x85040820080400L, 0x810102c808880400L, 0xe900410884800L, 0x8002020480840102L, 0x220200865090201L,
            0x2010100a02021202L, 0x152048408022401L, 0x20080002081110L, 0x4001001021004000L, 0x800040400a011002L,
            0xe4004081011002L, 0x1c004001012080L, 0x8004200962a00220L, 0x8422100208500202L, 0x2000402200300c08L,
            0x8646020080080080L, 0x80020a0200100808L, 0x2010004880111000L, 0x623000a080011400L, 0x42008c0340209202L,
            0x209188240001000L, 0x400408a884001800L, 0x110400a6080400L, 0x1840060a44020800L, 0x90080104000041L,
            0x201011000808101L, 0x1a2208080504f080L, 0x8012020600211212L, 0x500861011240000L, 0x180806108200800L,
            0x4000020e01040044L, 0x300000261044000aL, 0x802241102020002L, 0x20906061210001L, 0x5a84841004010310L,
            0x4010801011c04L, 0xa010109502200L, 0x4a02012000L, 0x500201010098b028L, 0x8040002811040900L,
            0x28000010020204L, 0x6000020202d0240L, 0x8918844842082200L, 0x4010011029020020L,
    };

    public static int seed = 1804289383;

    public static long getBishopRelevantOccupancyMask(int sq) {
        long result = 0L;
        int rk = sq / 8, fl = sq % 8, r, f;

        for(r = rk + 1, f = fl + 1; r <= 6 && f <= 6; r++, f++) result |= (1L << (f + r * 8));
        for(r = rk + 1, f = fl - 1; r <= 6 && f >= 1; r++, f--) result |= (1L << (f + r * 8));
        for(r = rk - 1, f = fl + 1; r >= 1 && f <= 6; r--, f++) result |= (1L << (f + r * 8));
        for(r = rk - 1, f = fl - 1; r >= 1 && f >= 1; r--, f--) result |= (1L << (f + r * 8));

        return result;
    }

    public static long getRookRelevantOccupancyMask(int sq) {
        long result = 0L;
        int rk = sq / 8, fl = sq % 8, r, f;

        for(r = rk + 1; r <= 6; r++) result |= (1L << (fl + r * 8));
        for(r = rk - 1; r >= 1; r--) result |= (1L << (fl + r * 8));
        for(f = fl + 1; f <= 6; f++) result |= (1L << (f + rk * 8));
        for(f = fl - 1; f >= 1; f--) result |= (1L << (f + rk * 8));

        return result;
    }

    public static long getBishopAttackMask(int sq, long block) {
        long result = 0L;
        int rk = sq / 8, fl = sq % 8, r, f;

        for(r = rk + 1, f = fl + 1; r <= 7 && f <= 7; r++, f++) {
            result |= (1L << (f + r * 8));
            if((block & (1L << (f + r * 8))) != 0) break;
        }
        for(r = rk + 1, f = fl - 1; r <= 7 && f >= 0; r++, f--) {
            result |= (1L << (f + r * 8));
            if((block & (1L << (f + r * 8))) != 0) break;
        }
        for(r = rk - 1, f = fl + 1; r >= 0 && f <= 7; r--, f++) {
            result |= (1L << (f + r * 8));
            if((block & (1L << (f + r * 8))) != 0) break;
        }
        for(r = rk - 1, f = fl - 1; r >= 0 && f >= 0; r--, f--) {
            result |= (1L << (f + r * 8));
            if((block & (1L << (f + r * 8))) != 0) break;
        }
        return result;
    }

    public static long getRookAttackMask(int sq, long block) {
        long result = 0L;
        int rk = sq / 8, fl = sq % 8, r, f;

        for(r = rk + 1; r <= 7; r++) {
            result |= (1L << (fl + r * 8));
            if((block & (1L << (fl + r * 8))) != 0) break;
        }
        for(r = rk - 1; r >= 0; r--) {
            result |= (1L << (fl + r * 8));
            if((block & (1L << (fl + r * 8))) != 0) break;
        }
        for(f = fl + 1; f <= 7; f++) {
            result |= (1L << (f + rk * 8));
            if((block & (1L << (f + rk * 8))) != 0) break;
        }
        for(f = fl - 1; f >= 0; f--) {
            result |= (1L << (f + rk * 8));
            if((block & (1L << (f + rk * 8))) != 0) break;
        }
        return result;
    }


    public static int generateRandom32BitNumber() {

        int number = seed;

        number ^= number << 13;
        number ^= number >>> 17;
        number ^= number << 5;

        seed = number;

        return number;
    }

    public static long generateRandom64BitNumber() {
        long n1, n2, n3, n4;

        n1 = generateRandom32BitNumber() & 0xFFFF;
        n2 = generateRandom32BitNumber() & 0xFFFF;
        n3 = generateRandom32BitNumber() & 0xFFFF;
        n4 = generateRandom32BitNumber() & 0xFFFF;

        return n1 | (n2 << 16) | (n3 << 32) | (n4 << 48);
    }

    public static long generate64BitWithLowNumberOfNonzeroBits() {
        return generateRandom64BitNumber() & generateRandom64BitNumber() & generateRandom64BitNumber();
    }

    public static long getOccupancyVariation(int index, int relevantBitNumber, long relevantOccupancyMask) {

        long occupancy = 0L;

        for (int count = 0; count < relevantBitNumber; count++) {
            int square = Long.numberOfTrailingZeros(relevantOccupancyMask);
            relevantOccupancyMask ^= Bitboard.toBit(square);

            if ((index & (1 << count)) != 0)
                occupancy |= 1L << square;
        }

        return occupancy;
    }

    public static long generateMagicNumber(int square, int relevantBitNumber, boolean isBishop) {

        long[] occupancies = new long[4096];
        long[] attacks = new long[4096];
        long[] usedAttacks;
        int index;
        boolean fail;

        long occupancyMask = isBishop ? getBishopRelevantOccupancyMask(square) : getRookRelevantOccupancyMask(square);

        for (index = 0; index < (1 << relevantBitNumber); index++) {
            occupancies[index] = getOccupancyVariation(index, relevantBitNumber, occupancyMask);
            attacks[index] = isBishop ? getBishopAttackMask(square, occupancies[index]) :
                                        getRookAttackMask(square, occupancies[index]);
        }

        while (true) {
            long magicNumber = generate64BitWithLowNumberOfNonzeroBits();

            if (Long.bitCount((occupancyMask * magicNumber) & 0xff00000000000000L) < 6) continue;
            usedAttacks = new long[4096];

            for (index = 0, fail = false; !fail && index < (1 << relevantBitNumber); index++) {

                int magicIndex = (int)((occupancies[index] * magicNumber) >>> (64 - relevantBitNumber));

                if (usedAttacks[magicIndex] == 0L)
                    usedAttacks[magicIndex] = attacks[index];
                else if (usedAttacks[magicIndex] != attacks[index])
                    fail = true;
            }

            if (!fail)
                return magicNumber;

        }

    }

    public static void printMagicNumbers() {
        System.out.println("Rook magic numbers:");
        for (int square = 0; square < 64; square++)
            System.out.printf("0x%xL,\n", generateMagicNumber(square, rookOccupancyBits[square], false));

        System.out.println("\nBishop magic numbers:");
        for (int square = 0; square < 64; square++)
            System.out.printf("0x%xL,\n", generateMagicNumber(square, bishopOccupancyBits[square], true));
    }

}
