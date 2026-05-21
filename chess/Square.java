package chess;

/**
 * Represents the Squares of a board
 * 
 * @param location location of the square on the board
 **/
public class Square {
	protected int[] location;
	
	protected Piece piece;
	
	// TODO Figure out powerups
	
	// Setters and getters
	void setLocation(int[] location) {
		this.location = location;
	}
	
	int[] getLocation() {
		return this.location;
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
			return Color.BLACK;
		} else {
			return Color.WHITE;
		}
	}
}

// TODO Special Squares
