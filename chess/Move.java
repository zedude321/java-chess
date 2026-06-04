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
	public int fromFile;
	public int fromRank;
	public boolean isCapture;
	public boolean isEnPassant;
	public boolean wasFirstMove;
	public Piece movedPiece;
	public Piece capturedPiece;
	public GameState gamestate;

	public Move() {
		fromFile = -1;
		fromRank = -1;
	}
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
	
	/**
	 * Parses algebraic chess notation to Move object.
	 * 
	 * @param notation algebraic chess notation
	 * @return Move object
	 */
	public static Move parse(String notation) {

        Move move = new Move();

        notation = notation.trim();
        
        // Victor
        if (notation.equals("1-0")) {
        	move.gamestate = GameState.WHITEWIN;
        	return move;
        }
        if (notation.equals("0.5-0.5") || notation.equals(".5-.5")) {
        	move.gamestate = GameState.DRAW;
        	return move;
        }
        if (notation.equals("0-1")) {
        	move.gamestate = GameState.BLACKWIN;
        	return move;
        }

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
	
	/**
	 * Turns Move object to algebraic chess notation
	 * @return Algebraic chess notation
	 */
	public String toNotation() {
	    StringBuilder sb = new StringBuilder();
	    
	    if (gamestate == GameState.WHITEWIN) {
	    	return "1-0";
	    }
	    if (gamestate == GameState.DRAW) {
	    	return "0.5-0.5";
	    }
	    if (gamestate == GameState.BLACKWIN) {
	    	return "0-1";
	    }

	    if (isShortCastle) {
	        return "O-O";
	    }

	    if (isLongCastle) {
	        return "O-O-O";
	    }

	    if (pieceType != null && pieceType != PieceType.PAWN) {
	        sb.append(pieceTypeToSymbol(pieceType));
	    }

	    if (fromFile != -1) {
	        sb.append((char) ('a' + fromFile));
	    }

	    if (fromRank != -1) {
	        sb.append(8 - fromRank);
	    }

	    if (isCapture) {
	        sb.append('x');
	    }

	    sb.append((char) ('a' + toFile));
	    sb.append(8 - toRank);

	    if (promotedPiece != null) {
	        sb.append('=');
	        sb.append(pieceTypeToSymbol(promotedPiece));
	    }

	    if (isCheckmate) {
	        sb.append('#');
	    } else if (isCheck) {
	        sb.append('+');
	    }

	    return sb.toString();
	}
	
	private static char pieceTypeToSymbol(PieceType type) {
	    switch (type) {
	        case KING:   return 'K';
	        case QUEEN:  return 'Q';
	        case ROOK:   return 'R';
	        case BISHOP: return 'B';
	        case KNIGHT: return 'N';
	        default: throw new IllegalArgumentException(
	            "Pawns have no symbol"
	        );
	    }
	}
	
	private static int fileToIndex(char file) {
        return file - 'a';
    }

    private static int rankToIndex(char rank) {
        return 8 - Character.getNumericValue(rank);
    }
}
