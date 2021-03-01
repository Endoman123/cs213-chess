package edu.rutgers.chess;

/**
 * Main runner class
 * 
 * @author Oscar Bartolo
 * @author Jared Tulayan
 */
public class Chess {
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        System.out.println(new Chess().getGreeting());
        System.out.println("Hello Again World!");
    }
}
