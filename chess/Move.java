package chess;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles move data within the game
 */
public class Move {
	public PieceType pieceType;
	public int toFile; // col
	public int toRank; // row
	public boolean isShortCastle;
	public boolean isLongCastle;
	public PieceType promotedPiece;
	public boolean isCheckmate;
	public boolean isCheck;
	public int fromFile = -1;
	public int fromRank = -1;
	public boolean isCapture;
	public boolean isEnPassant;
	public boolean wasFirstMove;
	public Piece movedPiece;
	public Piece capturedPiece;

	public Move() {}
	public Move(int fromRank, int fromFile, int toRank, int toFile) {
		this.fromFile = fromFile;
		this.toFile = toFile;
		this.fromRank = fromRank;
		this.toRank = toRank;
	}
	
	private static final Pattern MOVE_PATTERN = Pattern.compile(
            "^([KQRBN])?" +
            "([a-h])?" +
            "([1-8])?" +
            "(x)?" +
            "([a-h][1-8])" +
            "(=([QRBN]))?" +
            "([+#])?$"
    );
	
	public static Move parse(String notation) {

        Move move = new Move();

        notation = notation.trim();

        // Kingside castle
        if (notation.equals("O-O") || notation.equals("0-0")) {
            move.isShortCastle = true;
            move.pieceType = PieceType.KING;
            return move;
        }

        // Queenside castle
        if (notation.equals("O-O-O") || notation.equals("0-0-0")) {
            move.isLongCastle = true;
            move.pieceType = PieceType.KING;
            return move;
        }

        Matcher matcher = MOVE_PATTERN.matcher(notation);

        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                    "Invalid notation: " + notation
            );
        }

        // Piece
        move.pieceType = Piece.getTypeFromShortened(matcher.group(1));

        // From file
        if (matcher.group(2) != null) {
            move.fromFile = fileToIndex(
                    matcher.group(2).charAt(0)
            );
        }

        // From rank
        if (matcher.group(3) != null) {
            move.fromRank = rankToIndex(matcher.group(3).charAt(0));
        }

        // Capture
        move.isCapture = matcher.group(4) != null;

        // Destination
        String square = matcher.group(5);

        move.toFile = fileToIndex(square.charAt(0));
        move.toRank = rankToIndex(square.charAt(1));

        // Promotion
        if (matcher.group(7) != null) {
            move.promotedPiece = Piece.getTypeFromShortened(
                    matcher.group(7)
            );
        }

        // Check / mate
        String suffix = matcher.group(8);

        move.isCheck = "+".equals(suffix);
        move.isCheckmate = "#".equals(suffix);

        return move;
    }
	
	private static int fileToIndex(char file) {
        return file - 'a';
    }

    private static int rankToIndex(char rank) {
        return 8 - Character.getNumericValue(rank);
    }
}
