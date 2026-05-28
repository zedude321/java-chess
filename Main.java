import chess.*;
import javax.swing.*;
import java.util.Scanner;

/**
 * Entry point for the chess application.
 * Supports two modes: GUI mode (default) and terminal mode.
 * Pass the argument {@code --terminal} to play in the console instead.
 */
public class Main {

    /**
     * Launches the chess application in either GUI or terminal mode.
     * If no arguments are provided, the graphical interface is started.
     * If {@code --terminal} is passed as the first argument, the game
     * runs in the console using standard input.
     *
     * @param args command-line arguments. Use {@code --terminal} for console mode.
     */
    public static void main(String[] args) {
    	
    	if (args.length > 0 && args[0].equals("--terminal")) {
            Game game = new Game();
            game.initializeGame();
            Scanner sc = new Scanner(System.in);
            while (game.turn(sc)) {}
        } else {
            Game game = new Game();
            game.initializeGame();
            SwingUtilities.invokeLater(() -> new ChessGUI(game));
        }
    }
}
