package chess;

/**
 * Represents the Squares of a board
 * 
 * @param location location of the square on the board
 **/
public class Square {
	protected int[] location;
	
	// TODO Figure out powerups
	
	// Setters and getters
	void setLocation(int[] location) {
		this.location = location;
	}
	
	int[] getLocation() {
		return this.location;
	}
	
	// Static methods
	/**
	 * Calculates what color the square is
	 *
	 * @return returns the color of the square. Can be calculates so it isn't saved.
	 **/
	static Color getColor() {
		// TODO
	}
}

// TODO Special Squares
