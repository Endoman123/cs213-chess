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
     * @param b       the board context to test on
     * @return        a 64-bit bitboard representing tiles occupied by a piece
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
     * Get all the tiles that are in range of an attack by the specified team.
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

            // Skip pieces on the same team.
            if (Character.isUpperCase(b.getPiece(file, rank)) == isMajor)
                continue;

            if (
                isAttackedByPawn(b, file, rank, isMajor) || 
                isAttackedByKing(b, file, rank, isMajor))
                ret |= (1L << i);
        }

        return ret;
    }

    /**
     * Get whether or not a position is being attacked by a pawn.
     * 
     * @param b       the board context to test
     * @param file    the file to test
     * @param rank    the rank to test
     * @param isMajor whether or not the attacking piece is major
     * @return        true if the tile is at risk of being attacked by a pawn,
     *                false otherwise
     */
    public static boolean isAttackedByPawn(Board b, int file, int rank, boolean isMajor) {
        char attackingPiece = isMajor ? 'P' : 'p';
        int dir = isMajor ? 1 : -1;
        int safeRank = isMajor ? 1 : 8;

        // If we are out of the way of any pawn, we are safe.
        if (rank == safeRank)
            return false;

        if (file + 1 <= 8 && b.getPiece(file + 1, rank - dir) == attackingPiece)
            return true;

        if (file - 1 >= 1 && b.getPiece(file - 1, rank - dir) == attackingPiece)
            return true;

        return false;
    }

    /**
     * Get whether or not a position is being attacked by a king.
     * 
     * @param b       the board context to test
     * @param file    the file to test
     * @param rank    the rank to test
     * @param isMajor whether or not the attacking piece is major
     * @return        true if the tile is at risk of being attacked by a pawn,
     *                false otherwise
     */
    public static boolean isAttackedByKing(Board b, int file, int rank, boolean isMajor) {
        char attackingPiece = isMajor ? 'K' : 'k';
        int dir = isMajor ? 1 : -1;
        int safeRank = isMajor ? 1 : 8;

        // Kings can only attack pieces directly adjacent to them, simply check your surrounding area.
        for (int f = -1; f < 2; f++) {
            for (int r = -1; r < 2; r++) {
                // Don't check the same tile the king is on
                if (f == 0 && r == 0)
                    continue;
               
                // Make sure the tile is in range
                if (file + f < 1 || file + f > 8)
                    continue;

                if (rank + r < 1 || rank + r > 8)
                    continue;

                if (b.getPiece(file + f, rank + r) == attackingPiece)
                    return true;
            }
        }

        return false;
    }
}
