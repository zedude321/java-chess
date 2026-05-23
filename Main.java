import chess.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
    	Game game = new Game();
    	Scanner sc = new Scanner(System.in);
    	
    	game.initializeGame();
    	
    	while(game.turn(sc)) {
    		
    	}
    }
}
