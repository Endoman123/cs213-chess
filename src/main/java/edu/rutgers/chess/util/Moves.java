package edu.rutgers.chess.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

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
     * Generates all available moves given the current context
     * 
     * @param b the current board context
     * @return  all possible moves that can be made
     */
    public static String[] getMoves(Board b) {
        ArrayList<String> ret = new ArrayList<>();
        boolean isMajor = b.getIsMajorTurn();

        for(int i = 0; i < 64; i++) {
            int file = i % 8 + 1;
            int rank = i / 8 + 1;
            char c = b.getPiece(file, rank);

            if (c != ' ' && Character.isUpperCase(c) == isMajor) {
                String[] moves = new String[1];
                switch(Character.toUpperCase(c)) {
                    case 'B':
                        moves = getBishopMoves(b, file, rank);
                    break;
                    case 'K':
                        moves = getKingMoves(b, file, rank);
                    break;
                    case 'N':
                        moves = getKnightMoves(b, file, rank);
                    break;
                    case 'P':
                        moves = getPawnMoves(b, file, rank);
                    break;
                    case 'Q':
                        moves = getQueenMoves(b, file, rank);
                    break;
                    case 'R':
                        moves = getRookMoves(b, file, rank);
                    break;
                    default:
                        throw new IllegalStateException("Illegal character found: " + c);
                }

                ret.addAll(Arrays.asList(moves));
            }
        }

        return ret.toArray(new String[0]);
    }

    /**
     * Generates pawn moves for the given tile.
     * 
     * @param b    the board context for the pawn
     * @param file the file of the pawn
     * @param rank the rank of the pawn
     * @return     a list of moves that this pawn can make.
     */
    private static String[] getPawnMoves(Board b, int file, int rank) {
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
                    ret.add(encodeMove(file, rank, file, rank + dir, PROMOTION | SPECIAL_1 | SPECIAL_0));
                    ret.add(encodeMove(file, rank, file, rank + dir, PROMOTION | SPECIAL_1));
                    ret.add(encodeMove(file, rank, file, rank + dir, PROMOTION | SPECIAL_0));
                    ret.add(encodeMove(file, rank, file, rank + dir, PROMOTION));
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
                if (other != ' ' && isMajor != Character.isUpperCase(other)) {
                    if (rank + dir == promoRank) {
                        ret.add(encodeMove(file, rank, file + f, rank + dir, CAPTURE | PROMOTION | SPECIAL_1 | SPECIAL_0));
                        ret.add(encodeMove(file, rank, file + f, rank + dir, CAPTURE | PROMOTION | SPECIAL_1));
                        ret.add(encodeMove(file, rank, file + f, rank + dir, CAPTURE | PROMOTION | SPECIAL_0));
                        ret.add(encodeMove(file, rank, file + f, rank + dir, CAPTURE | PROMOTION));
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
     * Generates knight moves for the given tile.
     * 
     * @param b    the board context for the knight
     * @param file the file of the knight
     * @param rank the rank of the knight
     * @return     a list of moves that this knight can make.
     */
    public static String[] getKnightMoves(Board b, int file, int rank) {
        ArrayList<String> ret = new ArrayList<String>();
        char piece = b.getPiece(file, rank);
        boolean isMajor = Character.isUpperCase(piece);

        if (!"Nn".contains("" + piece))
            throw new IllegalArgumentException("Location does not refer to a knight!");

        // Check two steps in each cardinal direction, then one step in the orthogonal direction
        for (int f = -2; f < 3; f++) {
            if (f == 0 || file + f < 1 || file + f > 8)
                continue;

            int r = Math.abs(f) == 2 ? 1 : 2;

            if (rank + r <= 8) {
                char other = b.getPiece(file + f, rank + r);

                if (other == ' ' || Character.isUpperCase(other) != isMajor && Character.toUpperCase(other) != 'K')
                    ret.add(encodeMove(file, rank, file + f, rank + r, other == ' ' ? QUIET : CAPTURE));
            }

            if (rank - r >= 1) {
                char other = b.getPiece(file + f, rank - r);

                if (other == ' ' || Character.isUpperCase(other) != isMajor && Character.toUpperCase(other) != 'K')
                    ret.add(encodeMove(file, rank, file + f, rank - r, other == ' ' ? QUIET : CAPTURE));
            }
        }

        return filterIllegalMoves(b, file, rank, ret);
    }

    /**
     * Generates rook moves for the given tile.
     * 
     * @param b    the board context for the rook
     * @param file the file of the rook
     * @param rank the rank of the rook
     * @return     a list of moves that this rook can make.
     */
    public static String[] getRookMoves(Board b, int file, int rank) {
        ArrayList<String> ret = new ArrayList<String>();
        char piece = b.getPiece(file, rank);
        boolean isMajor = Character.isUpperCase(piece);

        if (!"Rr".contains("" + piece))
            throw new IllegalArgumentException("Location does not refer to a rook!");

        // Check the 4 cardinal directions around the rook
        // and perform a "raytrace" to the nearest piece.
        char other;
        for (int f = -1; f < 2; f++)  {
            for (int r = -1; r < 2; r++) {
                // Skip diagonals or center
                if (Math.abs(f) == Math.abs(r))
                    continue;

                int toFile = file;
                int toRank = rank;

                do {
                    toFile += f;
                    toRank += r;

                    // Check if we are out of range
                    if (toFile < 1 || toFile > 8 || toRank < 1 || toRank > 8)
                        break;
                    
                    other = b.getPiece(toFile, toRank);

                    if (other == ' ')
                        ret.add(encodeMove(file, rank, toFile, toRank, QUIET));
                    else if (Character.isUpperCase(other) != isMajor && Character.toUpperCase(other) != 'K')
                        ret.add(encodeMove(file, rank, toFile, toRank, CAPTURE));

                } while (other == ' ');
            }
        }

        return filterIllegalMoves(b, file, rank, ret);
    }

    /**
     * Generates bishop moves for the given tile.
     * 
     * @param b    the board context for the bishop
     * @param file the file of the bishop
     * @param rank the rank of the bishop
     * @return     a list of moves that this bishop can make.
     */
    public static String[] getBishopMoves(Board b, int file, int rank) {
        ArrayList<String> ret = new ArrayList<String>();
        char piece = b.getPiece(file, rank);
        boolean isMajor = Character.isUpperCase(piece);

        if (!"Bb".contains("" + piece))
            throw new IllegalArgumentException("Location does not refer to a bishop!");

        // Check the 4 diagonals around the bishop
        // and perform a "raytrace" to the nearest piece.
        char other;
        for (int f = -1; f < 2; f += 2)  {
            for (int r = -1; r < 2; r += 2) {
                // Skip [0, 0] as it is redundant
                if (f == 0 && r == 0)
                    continue;
               
                int toFile = file;
                int toRank = rank;

                do {
                    toFile += f;
                    toRank += r;

                    // Check if we are out of range
                    if (toFile < 1 || toFile > 8 || toRank < 1 || toRank > 8)
                        break;
                    
                    other = b.getPiece(toFile, toRank);

                    if (other == ' ')
                        ret.add(encodeMove(file, rank, toFile, toRank, QUIET));
                    else if (Character.isUpperCase(other) != isMajor && Character.toUpperCase(other) != 'K')
                        ret.add(encodeMove(file, rank, toFile, toRank, CAPTURE));

                } while (other == ' ');
            }
        }

        return filterIllegalMoves(b, file, rank, ret);
    }

    /**
     * Generates queen moves for the given tile.
     * 
     * @param b    the board context for the queen
     * @param file the file of the queen
     * @param rank the rank of the queen
     * @return     a list of moves that this queen can make.
     */
    public static String[] getQueenMoves(Board b, int file, int rank) {
        ArrayList<String> ret = new ArrayList<String>();
        char piece = b.getPiece(file, rank);
        boolean isMajor = Character.isUpperCase(piece);
        if (!"Qq".contains("" + piece))
            throw new IllegalArgumentException("Location does not refer to a queen!");

        // Check the 8 directions around the queen
        // and perform a "raytrace" to the nearest piece.
        char other;
        for (int f = -1; f < 2; f++)  {
            for (int r = -1; r < 2; r++) {
                // Skip [0, 0] as it is redundant
                if (f == 0 && r == 0)
                    continue;
               
                int toFile = file;
                int toRank = rank;

                do {
                    toFile += f;
                    toRank += r;

                    // Check if we are out of range
                    if (toFile < 1 || toFile > 8 || toRank < 1 || toRank > 8)
                        break;
                    
                    other = b.getPiece(toFile, toRank);

                    if (other == ' ')
                        ret.add(encodeMove(file, rank, toFile, toRank, QUIET));
                    else if (Character.isUpperCase(other) != isMajor && Character.toUpperCase(other) != 'K')
                        ret.add(encodeMove(file, rank, toFile, toRank, CAPTURE));

                } while (other == ' ');
            }
        }

        return filterIllegalMoves(b, file, rank, ret);
    }

    /**
     * Generates king moves for the given tile.
     * 
     * @param b    the board context for the king
     * @param file the file of the king
     * @param rank the rank of the king
     * @return     a list of moves that this king can make.
     */
    public static String[] getKingMoves(Board b, int file, int rank) {
        ArrayList<String> ret = new ArrayList<String>();
        char piece = b.getPiece(file, rank);
        boolean isMajor = Character.isUpperCase(piece);

        if (!"Kk".contains("" + piece))
            throw new IllegalArgumentException("Location does not refer to a king!");

        // Check the 8 squares around the king and make sure they are spaces the king can even move to.
        for (int f = -1; f < 2; f++)  {
            if (file + f < 1 || file + f > 8)
                continue;

            for (int r = -1; r < 2; r++) {
                // Don't check the same tile the king is on
                if (f == 0 && r == 0)
                    continue;
               
                // Make sure the tile is in range
                if (rank + r < 1 || rank + r > 8)
                    continue;

                char other = b.getPiece(file + f, rank + r);

                // Cannot attack kings
                if (other == (isMajor ? 'k' : 'K'))
                    continue;

                if (other == ' ')
                    ret.add(encodeMove(file, rank, file + f, rank + r, QUIET));

                else if (Character.isUpperCase(other) != isMajor) 
                    ret.add(encodeMove(file, rank, file + f, rank + r, CAPTURE));
            }
        }
        
        // Every castle has four conditions:
        // 1) The king and the rook may not have moved from their starting squares if you want to castle.
        // 2) All spaces between the king and the rook must be empty.
        // 3) The king cannot be in check.
        // 4) The squares that the king passes over must not be under attack, nor the square where it lands on.
        if (isMajor && (b.getCastles() & 0x8) != 0 || (b.getCastles() & 0x2) != 0) { // King side castle
            long testCheck = (0b01110000L << (rank * 8 - 8));
            long testEmpty = (0b01100000L << (rank * 8 - 8));

            if ((Bitboards.getAttackedTiles(b, !isMajor) & testCheck) == 0 && 
                (Bitboards.getOccupied(b) & testEmpty) == 0)
                    ret.add(encodeMove(file, rank, file + 2, rank, SPECIAL_1));
        } if (isMajor && (b.getCastles() & 0x4) != 0 || (b.getCastles() & 0x1) != 0) { // Queen side castle
            long testCheck = (0b00011100L << (rank * 8 - 8));
            long testEmpty = (0b00001110L << (rank * 8 - 8));

            if ((Bitboards.getAttackedTiles(b, !isMajor) & testCheck) == 0 && 
                (Bitboards.getOccupied(b) & testEmpty) == 0)
                ret.add(encodeMove(file, rank, file - 2, rank, SPECIAL_1 | SPECIAL_0));
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
        int kingPos = 0;
        long attackedTiles = 0;
        boolean isMajor = Character.isUpperCase(b.getPiece(file, rank));
        char curPiece = b.getPiece(file, rank);
        char curKing = isMajor ? 'K' : 'k';
        String memento = b.createMemento();

        // Step 1: Get the king's position if we aren't moving them
        if (curPiece != curKing) {
            for (kingPos = 0; kingPos < 64; kingPos++) {
                if (b.getPiece(kingPos % 8 + 1, kingPos / 8 + 1) == curKing)
                    break;
            }
        }

        // Step 2: Test all moves to see if the king is attacked in any of them
        while (i < curList.size()) {
            b.doMove(curList.get(i));
            attackedTiles = Bitboards.getAttackedTiles(b, !isMajor);

            // If we are moving the king,
            // we need to change the position that we check as we go
            if (curPiece == curKing) {
                int[] to = AlgebraicNotation.fromAN(curList.get(i).split(" ")[1]);
                kingPos = to[0] + to[1] * 8 - 9;
            }

            if ((attackedTiles & (1L << kingPos)) != 0)
                curList.remove(i);
            else
                i++;

            b.restore(memento); 
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
