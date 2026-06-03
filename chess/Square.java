package chess;

/**
 * Represents the Squares of a board
 * 
 * @param location location of the square on the board
 **/
public class Square {
	protected int row;
	protected int col;
	
	protected Piece piece;
	
	public Square(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	// Setters and getters
	void setLocation(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	int getRow() {
		return this.row;
	}
	
	int getCol() {
		return this.col;
	}
	
	void setPiece(Piece piece) {
		this.piece = piece;
	}
	
	Piece getPiece() {
		return this.piece;
	}
	
	// Static methods
	/**
	 * Calculates what color the square is
	 *
	 * @return returns the color of the square. Can be calculates so it isn't saved.
	 **/
	static Color getColor(int row, int col) {
		// TODO
		if (row % 2 == col % 2) {
			return Color.WHITE;
		} else {
			return Color.BLACK;
		}
	}
}

