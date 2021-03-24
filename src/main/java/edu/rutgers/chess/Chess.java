package edu.rutgers.chess;

import java.util.Arrays;
import java.util.Scanner;

import edu.rutgers.chess.util.AlgebraicNotation;
import edu.rutgers.chess.util.Bitboards;
import edu.rutgers.chess.util.Moves;

/**
 * Main runner class.
 * Input is parsed in this class.
 * 
 * @author Oscar Bartolo
 * @author Jared Tulayan
 */
public class Chess {
    public static void main(String[] args) {
        Board board = new Board();
        Scanner s = new Scanner(System.in);
        boolean canDraw = false;
        
        // Winner is determined with one of three values:
        // 1: Major (White)
        // 2: Minor (Black)
        // 3: Draw
        int winner = 0;

        do {
            String[] moves = Moves.getMoves(board);
            String move = null; 

            System.out.println(board);
            System.out.println();

            // Halfmove clock overrun, out of turns.
            if (board.getHalfmove() >= 100) {
                winner = 3;
                break;
            }

            // Test for check/checkmate
            if ((Bitboards.getAttackedTiles(board, !board.getIsMajorTurn()) & Bitboards.getTeamTiles(board, board.getIsMajorTurn()) & Bitboards.getOccupiedBy(board, 'K')) != 0) {
                if (moves.length == 0) {
                    System.out.println("Checkmate");
                    winner = board.getIsMajorTurn() ? 2 : 1;

                    break;
                } else
                    System.out.println("Check");
            }                

            if (moves.length > 0) {
                boolean requestDraw = false;

                while (move == null) {
                    // Bitboards.printBitboard(Bitboards.getAttackedTiles(board, !board.getIsMajorTurn()));
                    System.out.print(board.getIsMajorTurn() ? "White's move: " : "Black's move: ");
                    String[] inputs = s.nextLine().split(" ");
                    String testMove = null;

                    // Input won't be any more than 4 tokens
                    if (inputs.length > 0 && inputs.length < 5) {
                        // Test for resign or accepting draw
                        if (inputs.length == 1) {
                            if ("resign".equals(inputs[0])) {
                                move = "resign";
                                break;
                            } else if (canDraw && "draw".equals(inputs[0])) {
                                move = "draw";
                                break;
                            }
                        } else { // Parse move otherwise
                            testMove = inputs[0] + " " + inputs[1];

                            // Check input for promotion
                            if (inputs.length > 2 && "BNRQ".contains(inputs[2]))
                                testMove += " " + getPromoCode(inputs[0], inputs[1], inputs[2]);

                            // Check if asking for a draw
                            if ("draw?".equals(inputs[inputs.length - 1]))
                                requestDraw = true;
                        }
                    }

                    // Attempt to find a best-fit move
                    if (testMove != null) {
                        for (String m : moves) {
                            if (m.equals(testMove) || m.contains(testMove)) { 
                                move = m;    
                                break;
                            }
                        }
                    }

                    if (move == null)
                        System.out.println("Illegal move, try again");
                }

                // Perform the move
                switch(move) {
                    case "resign":
                        winner = board.getIsMajorTurn() ? 2 : 1;
                    break;
                    case "draw":
                        if (canDraw)
                            winner = 3;
                    break;
                    default:
                        if (requestDraw && !canDraw)
                            canDraw = true;
                        else
                            canDraw = false;
                        board.doMove(move);
                    break;
                }
            } else { // Draw due to stalemate
                System.out.println("Stalemate");
                winner = 3;
                break;
            }

            // Extra whitespace for readability
            System.out.println();
        } while (winner == 0);

        switch (winner) {
            case 1:
                System.out.println("White wins");
            break;
            case 2:
                System.out.println("Black wins");
            break;
            case 3:
                System.out.println("Draw");
            break;
        }

        s.close();
    }

    /**
     * Parses a pawn's promotion move to get the encoding flags for it.
     * 
     * @param from  the starting tile
     * @param to    the destination tile
     * @param promo input for determining the piece to promote to
     * @return      the bitflag encoding for the movement promotion
     */ 
    private static int getPromoCode(String from, String to, String extra) {
        int ret = Moves.PROMOTION;
        
        switch(extra) {
            case "B":
                ret |= Moves.SPECIAL_0;
            break;
            case "N":
            break;
            case "R":
                ret |= Moves.SPECIAL_1;
            break;
            case "Q":
                ret |= Moves.SPECIAL_1 | Moves.SPECIAL_0;
            break;
        }

        if (from.charAt(0) != to.charAt(0)) // Different file, most likely a capture
            ret |= Moves.CAPTURE;

        return ret;
    }
}
