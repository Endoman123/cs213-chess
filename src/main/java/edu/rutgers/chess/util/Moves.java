package edu.rutgers.chess.util;

import java.util.ArrayList;

import edu.rutgers.chess.Board;

/**
 * This class contains both fields that notate types of moves
 * as well as generators for moves for each type of piece.
 * <p>
 * The generators in this class treat file and rank as 1-indexed
 * indices.
 * <p>
 * All moves are generated as a string containing the starting tile,
 * the destination tile, and the flags denoting the nature of the move.
 * For example, a double-pawn push from b2 to b4 would be notated as the following:
 * <pre>
 * b2 b4 1
 * </pre>
 *  
 * @author Oscar Bartolo
 * @author Jared Tulayan
 */
public class Moves {
    /**
     * Bit flag that denotes a move being as a promotion
     */
    public static final byte PROMOTION = 0b1000;

    /**
     * Bit flag that denotes a move being made as a capture.
     * It can also be used as a third special flag.
     */
    public static final byte CAPTURE = 0b0100;

    /**
     * Bit flag used for miscellaneous encodings.
     */
    public static final byte SPECIAL_1 = 0b0010;

    /**
     * Bit flag used for miscellaneous encodings.
     */
    public static final byte SPECIAL_0 = 0b0001;

    /**
     * Bit flag representing a quiet move.
     */
    public static final byte QUIET = 0b0000;

    // Private constructor, should not be utilized
    private Moves() {}

    /**
     * Generates pawn moves for the given tile.
     * 
     * @param b    the board context for the pawn
     * @param file the file of the pawn
     * @param rank the rank of the pawn
     * @return     a list of moves that this pawn can make.
     */
    public static String[] gen_pawn_moves(Board b, int file, int rank) {
        ArrayList<String> ret = new ArrayList<String>();
        char piece = b.getPiece(file, rank);

        if (!"Pp".contains("" + piece))
            throw new IllegalArgumentException("Location does not refer to a pawn!");

        // Pawns are special pieces
        // in that they are the only pieces with two similar, but not identical
        // movesets that differ based on color.
        boolean isMajor = Character.isUpperCase(piece);
        int dir = isMajor ? 1 : -1;
        int promoRank = isMajor ? 8 : 1;

        // Quiet move
        if (b.getPiece(file, rank + dir) == ' ') {
            if (rank + dir == promoRank) {
                    ret.add(encodeMove(file, rank, file, rank + dir, PROMOTION));
                    ret.add(encodeMove(file, rank, file, rank + dir, PROMOTION | SPECIAL_0));
                    ret.add(encodeMove(file, rank, file, rank + dir, PROMOTION | SPECIAL_1));
                    ret.add(encodeMove(file, rank, file, rank + dir, PROMOTION | SPECIAL_1 | SPECIAL_0));
            } else
                ret.add(encodeMove(file, rank, file, rank + dir, QUIET));
        }

        // Double-pawn push
        if (rank == (isMajor ? 2 : 7) && b.getPiece(file, rank + dir * 2) == ' ')
            ret.add(encodeMove(file, rank, file, rank + dir * 2, SPECIAL_0));

        // Captures
        for (int f = -1; f < 2; f += 2) {
            if (file + f <= 8 && file + f >= 1) {
                char other = b.getPiece(file + f, rank + dir);

                // Exclude king captures
                if ("Kk".contains("" + other))
                    continue;

                // Basic capture
                if (isMajor != Character.isUpperCase(other)) {
                    if (rank + dir == promoRank) {
                        ret.add(encodeMove(file, rank, file + f, rank + dir, CAPTURE | PROMOTION));
                        ret.add(encodeMove(file, rank, file + f, rank + dir, CAPTURE | PROMOTION | SPECIAL_0));
                        ret.add(encodeMove(file, rank, file + f, rank + dir, CAPTURE | PROMOTION | SPECIAL_1));
                        ret.add(encodeMove(file, rank, file + f, rank + dir, CAPTURE | PROMOTION | SPECIAL_1 | SPECIAL_0));
                    } else
                        ret.add(encodeMove(file, rank, file + f, rank + dir, CAPTURE));
                }

                // En Passant capture
                else if (b.getEnPassant() == file + f + rank * 8 + dir * 8)
                    ret.add(encodeMove(file, rank, file + f, rank + dir, CAPTURE | SPECIAL_0));
            }
        }

        return filterIllegalMoves(b, file, rank, ret);
    }

    /**
     * Filter moves that would be considered illegal due to the move leaving the king in check.
     * 
     * @param b       the board context to test on
     * @param file    the file of the piece whose moves are listed
     * @param rank    the file of the piece whose moves are listed
     * @param curList the current list of moves to filter
     * @return        a filtered list of moves.
     */
    private static String[] filterIllegalMoves(Board b, int file, int rank, ArrayList<String> curList) {
        int i = 0;

        while (i < curList.size()) {
            // TODO: Move and restore
            i++;
        }

        return curList.toArray(new String[0]);
    }

    /**
     * Creates an encoded move based on the location and flags passed.
     * 
     * @param f1    the source file
     * @param r1    the source rank
     * @param f2    the destination file
     * @param r2    the destination rank
     * @param flags the bitflags denoting the nature of the move
     * @return      a move encoded as {@code "<from> <to> <flags>"} where
     *              {@code from} and {@code to} are both in algebraic notation,
     *              and {@code flags} is a byte encoded as an int.
     */
    private static String encodeMove(int f1, int r1, int f2, int r2, int flags) {
        return String.format(
            "%s %s %d", 
            AlgebraicNotation.toAN(f1, r1),
            AlgebraicNotation.toAN(f2, r2),
            (int)flags
        );
    }
}
