package edu.rutgers.chess;

/**
 * This class handles the backend of the chess game.
 * Inputs for the bindings of this class must be done in other objects.
 * 
 * @author Oscar Bartolo
 * @author Jared Tulayan
 */
public class Board {
    /**
     * The actual board, represented as chars.
     */
    private final char[][] BOARD;

    /**
     * Counter to keep track of the number of halfmoves made.
     */
    private byte halfmove;

    /**
     * Counter to keep track of the number of fullmoves made.
     */
    private byte fullmove;

    /**
     * Whether or not the major team (i.e.: white) is currently moving.
     */
    private boolean isMajorTurn;

    /**
     * The current tile that can be used in an en passant.
     * This is stored as a 1D tile index (i.e.: from 0 to 63).
     */
    private byte enPassant;

    /**
     * Bit flags representing the castles available.
     * From most significant bit to least significant bit,
     * the flags are major king, major queen, minor king, minor queen. 
     */
    private byte castles;

    /**
     * Constructs a new {@link Board} using a standard starting board.
     */
    public Board() {
       this(new char[][] {
           {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'},
           {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
           {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
           {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
           {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
           {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
           {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
           {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
       }); 
    }

    /**
     * Constructs a new {@link Board} given the starting state.
     * 
     * @param state the starting board state of the pieces, 
     *              must be an 8x8 char array using only {@code B, b, K, k, N, n, P, p, Q, q, R, r},
     *              or space to represent tiles not taken up by a piece.
     */
    public Board(char[][] state) {
        // Verify board
        if (state.length != 8)
            throw new IllegalArgumentException("Invalid board! Ranks != 8");
        for (char[] c : state)
            if (c.length != 8)
                throw new IllegalArgumentException("Invalid board! Files != 8");

        BOARD = state;

        // Starting state of the game
        halfmove = 0;
        fullmove = 1;
        isMajorTurn = true;
        enPassant = -1;
        castles = 0xf;
    }

    /**
     * Gets the piece on the tile sitting at the specified file and rank.
     * <p>
     * It should be noted that this is 1-indexed,
     * i.e.: files and ranks start at 1, not 0.
     * 
     * @param f the file of the tile to check
     * @param r the rank of the tile to check
     * @return  the piece on tile (file, rank)
     */
    public char getPiece(int f, int r) {
        if (f < 1 || f > 8)
            throw new IllegalArgumentException(String.format("File is out of range! f = %i", f));
        if (r < 1 || r > 8)
            throw new IllegalArgumentException(String.format("Rank is out of range! r = %i", r));

        return BOARD[f - 1][r - 1];
    }

    /**
     * Gets whether or not it is the major team's turn.
     * 
     * @return {@code true} if it is major team's turn,
     *         {@code false} otherwise.
     * 
     * @see #isMajorTurn
     */
    public boolean getIsMajorTurn() {
        return isMajorTurn;
    }

    /**
     * Gets the current en passant tile.
     * 
     * @return {@code -1} if there is no en passant this turn,
     *         otherwise returns the index of the en passant tile.
     * 
     * @see #enPassant
     */
    public byte getEnPassant() {
        return enPassant;
    }

    /**
     * Gets the castling abilities for both teams.
     * 
     * @return a byte representing the castling abilities as bit flags.
     * 
     * @see #castles
     */
    public byte getCastles() {
        return castles;
    }

    /**
     * Gets a string that represents the current board state.
     * 
     * @return an ASCII image of the board.
     */
    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (int i = 7; i > -1; i--) {
            for (int j = 0; j < 8; j++) {
                String toAppend = "  ";
                boolean isMajor = Character.isUpperCase(BOARD[i][j]);

                switch(Character.toUpperCase(BOARD[i][j])) {
                    case 'B':
                        toAppend = isMajor ? "wB" : "bB";
                        break; 
                    case 'K':
                        toAppend = isMajor ? "wK" : "bK";
                        break; 
                    case 'N':
                        toAppend = isMajor ? "wN" : "bN";
                        break; 
                    case 'P':
                        toAppend = isMajor ? "wp" : "bp";
                        break; 
                    case 'Q':
                        toAppend = isMajor ? "wQ" : "bQ";
                        break; 
                    case 'R':
                        toAppend = isMajor ? "wR" : "bR";
                        break; 
                    default:
                        if ((i % 2 == 0) == (j % 2 == 0))
                            toAppend = "##";
                }

                ret.append(toAppend);
                ret.append(" ");
            }
            ret.append(i + 1);
            ret.append(System.lineSeparator());
        }

        ret.append(" a  b  c  d  e  f  g  h");

        return ret.toString();
    }
}