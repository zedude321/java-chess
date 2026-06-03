package chess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
        bw.write("SPECIAL:" + board.getSpecialRow() + "," + board.getSpecialCol());
        bw.newLine();

        for (Move move : game.getTurnHistory()) {
            String line = move.fromRank + ","
                        + move.fromFile + ","
                        + move.toRank + ","
                        + move.toFile + ","
                        + (move.isShortCastle ? "S" : "-") + ","
                        + (move.isLongCastle ? "L" : "-") + ","
                        + (move.isEnPassant ? "E" : "-") + ","
                        + (move.promotedPiece != null ? move.promotedPiece.name() : "-");
            bw.write(line);
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
            game.getBoard().setSpecialSquare(row, col);
        }

        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");

            Move move = new Move();
            move.fromRank       = Integer.parseInt(parts[0]);
            move.fromFile       = Integer.parseInt(parts[1]);
            move.toRank         = Integer.parseInt(parts[2]);
            move.toFile         = Integer.parseInt(parts[3]);
            move.isShortCastle  = parts[4].equals("S");
            move.isLongCastle   = parts[5]. equals("L");
            move.isEnPassant    = parts[6].equals("E");
            move.promotedPiece  = parts[7].equals("-") ? null : PieceType.valueOf(parts[7]);

            Square fromSquare = game.getBoard().getSquare(move.fromRank, move.fromFile);
            if (fromSquare != null && fromSquare.getPiece() != null) {
                move.pieceType = fromSquare.getPiece().getPieceType();
            }

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
