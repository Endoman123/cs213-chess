package edu.rutgers.chess;

import edu.rutgers.chess.ChessHandler;

/**
 * Main runner class
 * 
 * @author Oscar Bartolo
 * @author Jared Tulayan
 */
public class Chess {
    public static void main(String[] args) {
        ChessHandler game = new ChessHandler();

        System.out.println(game.toString());
    }
}
