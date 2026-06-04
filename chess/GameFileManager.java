package chess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class GameFileManager {

    /**
     * Saves the current game state to a text file.
     * Writes the special square position and all moves played so far.
     * 
     * @param game          the Game to save
     * @param filename      the path of the output file
     * @throws IOException  if the file cannot be written
     */
    public static void saveGame (Game game, String filename) throws IOException {
        FileOutputStream fos = new FileOutputStream(filename);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        BufferedWriter bw = new BufferedWriter(osw);

        Board board = game.getBoard();
        bw.write("SPECIAL:" + board.getSpecialRow() + "," + board.getSpecialCol() + "," + game.getRandomSeed());
        bw.newLine();

        for (Move move : game.getTurnHistory()) {
        	bw.write(move.toNotation());
            bw.newLine();
        }

        bw.flush();
        bw.close();
        osw.close();
        fos.close();
    }

    public static void loadGame(Game game, String filename) throws IOException {
        FileInputStream fis = new FileInputStream(filename);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);

        game.initializeGame();

        String specialLine = br.readLine();
        if (specialLine != null && specialLine.startsWith("SPECIAL:")) {
            String[] parts = specialLine.substring(8).split(",");
            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);
            int randomSeed = Integer.parseInt(parts[2]);
            game.getBoard().setSpecialSquare(row, col);
            game.setRandomSeed(randomSeed);
        }

        String line;
        while ((line = br.readLine()) != null) {
            Move move = Move.parse(line);

            try {
                game.applyMove(move);
            } catch (InvalidMoveException e) {
                System.err.println("Could not replay move: " + line);
            }
        }

        br.close();
        isr.close();
        fis.close();
    }

    public static File getSavesFolder() {
        File folder = new File("saves");
        folder.mkdirs();
        return folder;
    }
}
