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
     * Get all the tiles that are in range of an attack by the specified team.
     * 
     * @param b       the board context to test on
     * @param isMajor whether or not the attacking team is major or minor
     * @return        a 64-bit bitboard representing attacked tiles, ordered from a1 to h8
     */
    public static long getAttackedTiles(Board b, boolean isMajor) {
        long ret = 0L;

        for (int i = 0; i < 64; i++) {
            int file = i % 8 + 1;
            int rank = i / 8 + 1;

            // Skip pieces on the same team.
            if (Character.isUpperCase(b.getPiece(file, rank)) == isMajor)
                continue;

            if (isAttackedByPawn(b, file, rank, isMajor))
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
        boolean ret = false;
        char attackingPiece = isMajor ? 'P' : 'p';
        int dir = isMajor ? 1 : -1;
        int safeRank = isMajor ? 1 : 8;

        // If we are out of the way of any pawn, we are safe.
        if (rank == safeRank)
            return false;

        if (file + 1 <= 8)
            ret |= b.getPiece(file + 1, rank - dir) == attackingPiece;

        if (file - 1 >= 1)
            ret |= b.getPiece(file - 1, rank - dir) == attackingPiece;

        return ret;
    }
}
