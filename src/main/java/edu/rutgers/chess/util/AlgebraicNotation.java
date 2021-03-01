package edu.rutgers.chess.util;

/**
 * Utility class to read/write algebraic notation.
 * <p>
 * Algebraic notation is the way of notating the file and rank of a board position. 
 * The file refers to the board columns, and the ranks the board rows,
 * i.e.: the file is the x-position, the rank the y-position. 
 * Files are notated alphabetically, ranks numerically.
 * <p>
 * e.g.: b7 would refer to (2, 7) on a chess board.
 * 
 * @author Oscar Bartolo
 * @author Jared Tulauan
 */
public final class AlgebraicNotation {
    // Hide constructor; this is a utility class
    private AlgebraicNotation() {} 

    /**
     * Writes a grid location to algebraic notation.
     * <p>
     * It should be noted that this is 1-indexed,
     * i.e.: files and ranks start at 1, not 0.
     * 
     * @param f the file
     * @param r the rank
     * @return  the file and rank in AN notation
     */
    public static String toAN(int f, int r) {
        if (f < 1 || f > 8)
            throw new IllegalArgumentException(String.format("File is out of range! f = %i", f));
        if (r < 1 || r > 8)
            throw new IllegalArgumentException(String.format("Rank is out of range! r = %i", r));
        
        return "" + (char)(96 + f) + r;
    }


    /**
     * Gets a grid location from algebraic notation.
     * <p>
     * It should be noted that this is 1-indexed,
     * i.e.: files and ranks start at 1, not 0.
     * <p>
     * The input must also be trimmed before passing.
     * 
     * @param an the coordinate in algebraic notation
     * @return   the file and rank in an int array,
     *           as {@code [file, rank]}
     */
    public static int[] fromAN(String an) {
        if (!an.matches("[a-h][1-8]"))
            throw new IllegalArgumentException("Algebraic notation is invalid!");

        return new int[]{(int)an.charAt(0) - 96, (int)an.charAt(1) - 48};
    }
}