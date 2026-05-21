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
	protected Square square;
	protected final Color color;
	protected boolean hasMoved = false;
	
	
	public Piece(Square square, Color color) {
		this.square = square;
		this.color = color;
	}

	public abstract PieceType getPieceType();
    public abstract String getShortenedType();
    
	/**
	 * Calculates all moves regardless of legality
	 *
	 * @return returns a list of squares it can move to. Disregards validity
	 **/
	public abstract ArrayList<Square> getValidMoves(Board board);
	/**
	 * Calculates all attacking moves regardless of legality
	 *
	 * @return returns a list of squares it can attack to. Different from moves for the case of Pawns
	 **/
	public ArrayList<Square> getAttackingMoves(Board board) {
		return this.getValidMoves(board);
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
	
	
	Color getColor() {
		return this.color;
	}
	// Static methods
	/**
	 * Returns the type of piece from the chess notation
	 *
	 * @return returns the piece type
	 **/
	static PieceType getTypeFromShortened(String shortened) {
		// TODO
		switch(shortened) {
		case "K": 
			return PieceType.KING;
		case "Q":
			return PieceType.QUEEN;
		case "R":
			return PieceType.ROOK;
		case "B":
			return PieceType.BISHOP;
		case "N":
			return PieceType.KNIGHT;
		default:
			return PieceType.PAWN;
		}
	}
}

class Queen extends Piece {
	
	public Queen(Square square, Color color) {
		super(square, color);
	}
	@Override
    public PieceType getPieceType() {
        return PieceType.QUEEN;
    }

    @Override
    public String getShortenedType() {
        return "Q";
    }
	public ArrayList<Square> getValidMoves(Board board) {
		// TODO
		ArrayList<Square> validMoves = new ArrayList<>();
		int[][] directions = {
				{-1, 0},
				{1, 0},
				{0, -1},
				{0, 1},
				
				{-1, -1},
				{-1, 1},
				{1, -1},
				{1, 1}
		};
		
		int row = square.getLocation()[0];
		int col = square.getLocation()[1];
		
		for (int[] dir : directions) {
			int newRow = row + dir[0];
			int newCol = col + dir[1];
			
			while (newRow >= 0 && newRow < 8 &&
				   newCol >= 0 && newCol < 8) {
				Square targetSquare = board.getSquare(newRow, newCol);
				validMoves.add(targetSquare);
				
				if (targetSquare.getPiece() != null) {
					break;
				}
				
				newRow += dir[0];
				newCol += dir[1];
			}
		}
		return validMoves;
	}
}

class Bishop extends Piece {
	public Bishop(Square square, Color color) {
		super(square, color);
	}
	@Override
	public PieceType getPieceType() {
	    return PieceType.BISHOP;
	}

	@Override
	public String getShortenedType() {
	    return "B";
	}
	
	public ArrayList<Square> getValidMoves(Board board) {
		// TODO
		ArrayList<Square> validMoves = new ArrayList<>();
		int[][] directions = {
				{-1, -1},
				{-1, 1},
				{1, -1},
				{1, 1}
		};
		
		int row = square.getLocation()[0];
		int col = square.getLocation()[1];
		
		for (int[] dir : directions) {
			int newRow = row + dir[0];
			int newCol = col + dir[1];
			
			while (newRow >= 0 && newRow < 8 &&
				   newCol >= 0 && newCol < 8) {
				Square targetSquare = board.getSquare(newRow, newCol);
				validMoves.add(targetSquare);
				
				if (targetSquare.getPiece() != null) {
					break;
				}
				
				newRow += dir[0];
				newCol += dir[1];
			}
		}
		return validMoves;
	}
}

class Knight extends Piece {
	public Knight(Square square, Color color) {
		super(square, color);
	}
	@Override
	public PieceType getPieceType() {
	    return PieceType.KNIGHT;
	}

	@Override
	public String getShortenedType() {
	    return "N";
	}
	
	public ArrayList<Square> getValidMoves(Board board) {
		// TODO
		ArrayList<Square> validMoves = new ArrayList<>();
		int[][] offsets = {
				{2, 1},
				{2, -1},
				{-2, 1},
				{-2, -1},
				{1, 2},
				{1, -2},
				{-1, 2},
				{-1, -2}
		};
		
		int row = square.getLocation()[0];
		int col = square.getLocation()[1];
		
		for (int[] offset : offsets) {
			int newRow = row + offset[0];
			int newCol = col + offset[1];
			
			if (newRow >= 0 && newRow < 8 &&
				newCol >= 0 && newCol < 8) {
				validMoves.add(board.getSquare(newRow, newCol));
			}
		}
		return validMoves;
	}
}

class Rook extends Piece {
	public Rook(Square square, Color color) {
		super(square, color);
	}
	@Override
	public PieceType getPieceType() {
	    return PieceType.ROOK;
	}

	@Override
	public String getShortenedType() {
	    return "R";
	}
	
	public ArrayList<Square> getValidMoves(Board board) {
		// TODO
		ArrayList<Square> validMoves = new ArrayList<>();
		int[][] directions = {
				{-1, 0},
				{1, 0},
				{0, -1},
				{0, 1},
		};
		
		int row = square.getLocation()[0];
		int col = square.getLocation()[1];
		
		for (int[] dir : directions) {
			int newRow = row + dir[0];
			int newCol = col + dir[1];
			
			while (newRow >= 0 && newRow < 8 &&
				   newCol >= 0 && newCol < 8) {
				Square targetSquare = board.getSquare(newRow, newCol);
				validMoves.add(targetSquare);
				
				if (targetSquare.getPiece() != null) {
					break;
				}
				
				newRow += dir[0];
				newCol += dir[1];
			}
		}
		return validMoves;
	}
}

class Pawn extends Piece {
	public Pawn(Square square, Color color) {
		super(square, color);
	}
	@Override
	public PieceType getPieceType() {
	    return PieceType.PAWN;
	}

	@Override
	public String getShortenedType() {
	    return "";
	}
	public ArrayList<Square> getValidMoves(Board board) {
		// TODO
		ArrayList<Square> validMoves = new ArrayList<>();
		int row = square.getLocation()[0];
		int col = square.getLocation()[1];
		
		int direction;
		
		if (color == Color.WHITE) {
			direction = -1;
		} else {
			direction = 1;
		}
		int newRow = row + direction;
		
		if (newRow >= 0 && newRow < 8) {
			Square targetSquare = board.getSquare(newRow, col);
			
			if (targetSquare.getPiece() == null) {
				validMoves.add(targetSquare);
				if (!hasMoved) {
					
					int doubleRow = row + 2 * direction;
					Square doubleSquare = board.getSquare(doubleRow, col);
					if (doubleSquare.getPiece() == null) {
						validMoves.add(doubleSquare);
					}
				}
			}
			
			int[] diagonalCols = {col - 1, col + 1};
			
			for (int diagonalCol : diagonalCols) {
				if (diagonalCol < 0 || diagonalCol >= 8 ||
					newRow < 0 || newRow >= 8) {
					continue;
				}
				Square diagonalSquare = board.getSquare(newRow, diagonalCol);
				Piece targetPiece = diagonalSquare.getPiece();
				
				if (targetPiece != null && targetPiece.getColor() != color) {
					validMoves.add(diagonalSquare);
				}
				if (diagonalSquare == board.getEnPassantTarget()) {
					validMoves.add(diagonalSquare);
				}
			}
		}
		return validMoves;
	}
	
	@Override
	public ArrayList<Square> getAttackingMoves(Board board) {
		// TODO
		ArrayList<Square> attackingMoves = new ArrayList<>();
		int row = square.getLocation()[0];
		int col = square.getLocation()[1];
		
		int direction;
		
		if (color == Color.WHITE) {
			direction = -1;
		} else {
			direction = 1;
		}
		int newRow = row + direction;
		if (newRow >= 0 && newRow < 8) {
			
			int[] diagonalCols = {col - 1, col + 1};
			
			for (int diagonalCol : diagonalCols) {
				if (diagonalCol < 0 || diagonalCol >= 8 ||
					newRow < 0 || newRow >= 8) {
					continue;
				}
				Square diagonalSquare = board.getSquare(newRow, diagonalCol);
				Piece targetPiece = diagonalSquare.getPiece();
				
				if (targetPiece != null && targetPiece.getColor() != color) {
					attackingMoves.add(diagonalSquare);
				}
				if (diagonalSquare == board.getEnPassantTarget()) {
					attackingMoves.add(diagonalSquare);
				}
			}
		}
		return attackingMoves;
	}
	
	public void promote() {
		// TODO
	}

}

class King extends Piece {
	public King(Square square, Color color) {
		super(square, color);
	}
	@Override
	public PieceType getPieceType() {
	    return PieceType.KING;
	}

	@Override
	public String getShortenedType() {
	    return "K";
	}
	public ArrayList<Square> getValidMoves(Board board) {
		// TODO + castling
		ArrayList<Square> validMoves = new ArrayList<>();
		int[][] directions = {
				{-1, -1},
				{-1, 0},
				{-1, 1},
				{0, -1},
				{0, 1},
				{1, -1},
				{1, 0},
				{1, 1}
		};
		int row = square.getLocation()[0];
		int col = square.getLocation()[1];
		
		for (int[] dir : directions) {
			int newRow = row + dir[0];
			int newCol = col + dir[1];
			
			if (newRow >= 0 && newRow < 8 &&
				newCol >= 0 && newCol < 8) {
				validMoves.add(board.getSquare(newRow, newCol));
			}
		}
		return validMoves;
	}

}
