package edu.rutgers.chess;

import edu.rutgers.chess.Board;

/**
 * Main runner class
 * 
 * @author Oscar Bartolo
 * @author Jared Tulayan
 */
public class Chess {
    public static void main(String[] args) {
        Board board = new Board();

        System.out.println(board.toString());
        System.out.println(board.createMemento());
    }
}
