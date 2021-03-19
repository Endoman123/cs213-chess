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
        boolean askDraw = false;
        
        // Winner is determined with one of three values:
        // 1: Major (White)
        // 2: Minor (Black)
        // 3: Draw
        int winner = 0;

        do {
            String[] moves = Moves.getMoves(board);
            String move = null; 

            System.out.println(board);

            // Halfmove clock overrun, out of turns.
            if (board.getHalfmove() >= 100) {
                winner = 3;
                break;
            }

            // Checkmate or stalemate, it's one of the two.
            if (moves.length == 0) {
                if ((Bitboards.getAttackedTiles(board, !board.getIsMajorTurn()) & Bitboards.getTeamTiles(board, board.getIsMajorTurn()) & Bitboards.getOccupiedBy(board, 'K')) != 0)
                    winner = board.getIsMajorTurn() ? 2 : 1;
                else
                    winner = 3;
                
                break;
            }

            while (move == null) {
                System.out.println(board.getIsMajorTurn() ? "White's move: " : "Black's move: ");
                String[] inputs = s.nextLine().split(" ");

                // Input won't be any more than 4 tokens
                if (inputs.length > 0 && inputs.length < 5) {
                    // Test for resign or accepting draw
                    if (inputs.length == 1) {
                        if ("resign".equals(inputs[0])) {
                            move = "resign";
                            break;
                        } else if (askDraw && "draw".equals(inputs[0])) {
                            move = "draw";
                            break;
                        }
                    } else { // Parse move otherwise
                        move = inputs[0] + " " + inputs[1];
                        if ("BNRQ".contains(inputs[2]))
                            move += getPromoCode(inputs[0], inputs[1], inputs[2]);

                        // Check if asking for a draw
                        if ("draw?".equals(inputs[inputs.length - 1]))
                            askDraw = true;
                    }
                }

                if (move == null)
                    System.out.println("Illegal move, try again");
            }

            switch(move) {
                case "resign":
                    winner = board.getIsMajorTurn() ? 2 : 1;
                break;
                case "draw":
                    if (askDraw)
                        winner = 3;
                break;
                default:
                    board.doMove(move);
                break;
            }
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
