package edu.rutgers.chess.util;

import edu.rutgers.chess.Board;

/**
 * Utility class to convert a chessboard to bitboards.
 * <p>
 * All bitboards are 64-bit longs, representing a board from a1 to h8
 * from the least significant bit to the most significant bit.
 * 
 * @author Oscar Bartolo
 * @author Jared Tulayan
 */
public class Bitboards {
    /**
     * Get all occupied tiles.
     * 
     * @param b the board context to test on
     * @return  a 64-bit bitboard representing tiles occupied by a piece
     */
    public static long getOccupied(Board b) {
        long ret = 0L;

        for (int i = 0; i < 64; i++) {
            int file = i % 8 + 1;
            int rank = i / 8 + 1;

            if (b.getPiece(file, rank) != ' ')
                ret |= (1L << i);
        }

        return ret;
    }

    /**
     * Get all the tiles that are occupied by the specified team.
     * 
     * @param b       the board context to test on
     * @param isMajor whether or not the team is major or minor
     * @return        a 64-bit bitboard representing tiles occupied by the specified team
     */
    public static long getTeamTiles(Board b, boolean isMajor) {
        long ret = 0L;

        for (int i = 0; i < 64; i++) {
            int file = i % 8 + 1;
            int rank = i / 8 + 1;
            char c = b.getPiece(file, rank);

            if (c != ' ' && Character.isUpperCase(c) == isMajor)
                ret |= (1L << i);
        }

        return ret;
    }

    /**
     * Get all the tiles that are occupied by the given piece.
     * 
     * @param b the board context to test on
     * @param c the piece to look for, case insenstivie
     * @return  a 64-bit bitboard representing tiles occupied by piece {@code c}
     */
    public static long getOccupiedBy(Board b, char c) {
        long ret = 0L;

        for (int i = 0; i < 64; i++) {
            int file = i % 8 + 1;
            int rank = i / 8 + 1;
            char piece = b.getPiece(file, rank);

            if (Character.toUpperCase(c) == Character.toUpperCase(piece))
                ret |= (1L << i);
        }

        return ret;
    }

    /**
     * Get all the tiles that are in range of an attack by the specified team.
     * <p>
     * Note that this will count tiles occupied by the same team that are in the path of attack.
     *  
     * @param b       the board context to test on
     * @param isMajor whether or not the attacking team is major or minor
     * @return        a 64-bit bitboard representing attacked tiles
     */
    public static long getAttackedTiles(Board b, boolean isMajor) {
        long ret = 0L;

        for (int i = 0; i < 64; i++) {
            int file = i % 8 + 1;
            int rank = i / 8 + 1;
            char piece = b.getPiece(file, rank);

            if (piece != ' ' && Character.isUpperCase(piece) == isMajor) {
                long occupied = getOccupied(b);
                switch(Character.toUpperCase(piece)) {
                    case 'B':
                        for (int f = -1; f < 2; f += 2) {
                            for (int r = -1; r < 2; r += 2) {
                                int curFile = file - 1;
                                int curRank = rank - 1;
                                long curTile;

                                do {
                                    curFile += f;
                                    curRank += r;

                                    if (curFile > 7 || curFile < 0 || curRank > 7 || curRank < 0)
                                        break;

                                    curTile = (1L << (curFile + curRank * 8));
                                    ret |= curTile;
                                } while ((occupied & curTile) == 0);
                            }
                        }
                    break;
                    case 'K':
                        for (int f = -1; f < 2; f++) {
                            if (file + f > 8 || file + f < 1)
                                continue;

                            for (int r = -1; r < 2; r++) {
                                if (f == 0 && r == 0)
                                    continue;

                                if (rank + r > 8 || rank + r < 1)
                                    continue;

                                ret |= (1L << file + f + rank * 8 + r * 8 - 9);
                            }
                        }
                    break;
                    case 'N':
                        for (int f = -2; f < 3; f++) {
                            if (file + f > 8 || file + f < 1 || f == 0)
                                continue;

                            int r = Math.abs(f) == 2 ? 1 : 2;

                            if (rank + r <= 8)
                                ret |= (1L << file + f + rank * 8 + r * 8 - 9);
                            if (rank - r >= 1)
                                ret |= (1L << file + f + rank * 8 - r * 8 - 9);
                        }
                    break;
                    case 'P':
                        int dir = isMajor ? 1 : -1;

                        if (file + 1 <= 8)
                            ret |= (1L << file + rank * 8 + dir * 8 - 8);
                        if (file - 1 >= 1)
                            ret |= (1L << file + rank * 8 + dir * 8 - 10);
                    break;
                    case 'R':
                        for (int f = -1; f < 2; f++) {
                            for (int r = -1; r < 2; r++) {
                                if (Math.abs(f) == Math.abs(r))
                                    continue;

                                int curFile = file - 1;
                                int curRank = rank - 1;
                                long curTile;

                                do {
                                    curFile += f;
                                    curRank += r;

                                    if (curFile > 7 || curFile < 0 || curRank > 7 || curRank < 0)
                                        break;

                                    curTile = (1L << curFile + curRank * 8);
                                    ret |= curTile;
                                } while ((occupied & curTile) == 0);
                            }
                        }
                    break;
                    case 'Q':
                        for (int f = -1; f < 2; f++) {
                            for (int r = -1; r < 2; r++) {
                                if (f == 0 && r == 0)
                                    continue;

                                int curFile = file - 1;
                                int curRank = rank - 1;
                                long curTile;

                                do {
                                    curFile += f;
                                    curRank += r;

                                    if (curFile > 7 || curFile < 0 || curRank > 7 || curRank < 0)
                                        break;

                                    curTile = (1L << curFile + curRank * 8);
                                    ret |= curTile;
                                } while ((occupied & curTile) == 0);
                            }
                        }
                    break;
                }
            }
        }

        return ret;
    }

    public static void printBitboard(Long bb) {
        StringBuilder build = new StringBuilder(Long.toBinaryString(bb));

        while (build.length() < 64) {
            build.insert(0, "0");
        }

        build.insert(56, System.lineSeparator());
        build.insert(48, System.lineSeparator());
        build.insert(40, System.lineSeparator());
        build.insert(32, System.lineSeparator());
        build.insert(24, System.lineSeparator());
        build.insert(16, System.lineSeparator());
        build.insert(8, System.lineSeparator());

        System.out.println(build.toString());
    }
}
