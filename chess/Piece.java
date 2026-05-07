package chess;
import java.util.ArrayList;

enum PieceType {
	KING,
	QUEEN,
	ROOK,
	BISHOP,
	KNIGHT,
	PAWN
}

enum Color {
	BLACK,
	WHITE
}

/**
 * Represents a chess piece
 * 
 * @param type the type of piece it is
 * @param shortenedType the letter notation of the piece
 * @param square the square the piece is on. Useful for powered squares
 * @param color the color the piece belongs to
 * @param hasMoved important for move validation (en passant, castling)
 * */
abstract class Piece {
	protected final PieceType type = null;
	protected final String shortenedType = null;
	protected Square square;
	protected final Color color;
	protected boolean hasMoved = false;
	
	public Piece(Square square, Color color) {
		this.square = square;
		this.color = color;
	}
	
	/**
	 * Calculates all moves regardless of legality
	 *
	 * @return returns a list of squares it can move to. Disregards validity
	 **/
	public abstract ArrayList<Square> getValidMoves();
	/**
	 * Calculates all attacking moves regardless of legality
	 *
	 * @return returns a list of squares it can attack to. Different from moves for the case of Pawns
	 **/
	public ArrayList<Square> getAttackingMoves() {
		return this.getValidMoves();
	}
	
	// Setters and getters
	void setHasMoved(boolean newValue) {
		this.hasMoved = newValue;
	}
	
	boolean getHasMoved() {
		return this.hasMoved;
	}
	
	void setSquare(Square square) {
		this.square = square;
	}
	
	Square getSquare() {
		return this.square;
	}
	
	String getShortenedType() {
		return this.shortenedType;
	}
	
	Color getColor() {
		return this.color;
	}
	
	PieceType getPieceType() {
		return this.type;
	}
	
	// Static methods
	/**
	 * Returns the type of piece from the chess notation
	 *
	 * @return returns the piece type
	 **/
	static PieceType getTypeFromShortened(String shortened) {
		// TODO
	}
}

class Queen extends Piece {
	protected final PieceType type = PieceType.QUEEN;
	protected final String shortenedType = "Q";

	public Queen(Square square, Color color) {
		super(square, color);
	}
	
	public ArrayList<Square> getValidMoves() {
		// TODO
	}
}

class Bishop extends Piece {
	protected final PieceType type = PieceType.BISHOP;
	protected final String shortenedType = "B";

	public Bishop(Square square, Color color) {
		super(square, color);
	}
	
	public ArrayList<Square> getValidMoves() {
		// TODO
	}
}

class Knight extends Piece {
	protected final PieceType type = PieceType.KNIGHT;
	protected final String shortenedType = "N";

	public Knight(Square square, Color color) {
		super(square, color);
	}
	
	public ArrayList<Square> getValidMoves() {
		// TODO
	}
}

class Rook extends Piece {
	protected final PieceType type = PieceType.ROOK;
	protected final String shortenedType = "R";

	public Rook(Square square, Color color) {
		super(square, color);
	}
	
	public ArrayList<Square> getValidMoves() {
		// TODO
	}
}

class Pawn extends Piece {
	protected final PieceType type = PieceType.PAWN;
	protected final String shortenedType = "";

	public Pawn(Square square, Color color) {
		super(square, color);
	}
	
	public ArrayList<Square> getValidMoves() {
		// TODO
	}
	
	@Override
	public ArrayList<Square> getAttackingMoves() {
		// TODO
	}
	
	public void promote() {
		// TODO
	}
}

class King extends Piece {
	protected final PieceType type = PieceType.KING;
	protected final String shortenedType = "K";

	public King(Square square, Color color) {
		super(square, color);
	}
	
	public ArrayList<Square> getValidMoves() {
		// TODO + castling
	}
}
