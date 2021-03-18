package edu.rutgers.chess;

import java.util.Arrays;

import edu.rutgers.chess.Board;
import edu.rutgers.chess.util.Bitboards;
import edu.rutgers.chess.util.Moves;

/**
 * Main runner class
 * 
 * @author Oscar Bartolo
 * @author Jared Tulayan
 */
public class Chess {
    public static void main(String[] args) {
        Board board = new Board(new char[][] {
           {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'},
           {'P', 'P', 'P', 'P', ' ', 'P', 'P', 'P'},
           {' ', ' ', ' ', ' ', 'P', ' ', ' ', ' '},
           {' ', ' ', ' ', ' ', ' ', ' ', ' ', 'q'},
           {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
           {' ', ' ', ' ', ' ', 'p', ' ', ' ', ' '},
           {'p', 'p', 'p', 'p', ' ', 'p', 'p', 'p'},
           {'r', 'n', 'b', ' ', 'k', 'b', 'n', 'r'},
       });

        System.out.println(board.toString());
        System.out.println(Arrays.toString(Moves.gen_pawn_moves(board, 6, 2)));
    }
}
