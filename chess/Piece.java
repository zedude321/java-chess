package chess;
import java.util.ArrayList;
import java.util.Iterator;

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
 * @param color the color the piece belongs to
 * @param hasMoved important for move validation (en passant, castling)
 * @param directions contains movement directions not used for knights and pawns
 * @param singleMoves contains information on whether piece moves infinitely or not. Not used for pawns and knights.
 * */
abstract class Piece {
	protected final Color color;
	protected boolean hasMoved = false;
	protected int[][] directions;
	protected boolean singleMoves;
	
	public Piece(Color color) {
		this.color = color;
	}

	public abstract PieceType getPieceType();
    public abstract String getShortenedType();
    
	/**
	 * Calculates all moves regardless of legality
	 *
	 * @return returns a list of squares it can move to. Disregards checks
	 **/
    public ArrayList<Move> getValidMoves(Board board, Square square) {
		ArrayList<Move> moves = generateMoves(board, square);
		Iterator<Move> iterator = moves.iterator();

		while (iterator.hasNext()) {
		    Move move = iterator.next();
		    Square curSquare = board.getSquare(move.toRank, move.toFile);
		    
		    if (curSquare.getPiece() == null) continue;

		    if (curSquare.getPiece().getColor() == this.color) {
		        iterator.remove();
		    } else {
		    	move.isCapture = true;
		    }
		}
		
		return moves;
	}
	/**
	 * Calculates all attacking moves regardless of legality
	 *
	 * @return returns a list of squares it can attack to. Different from moves for the case of Pawns
	 **/
	public ArrayList<Move> getAttackingMap(Board board, Square square) {
		return generateMoves(board, square);
	}
	/**
	 * Generates all target moves regardless of legality or same color pieces
	 *
	 * @return returns a list of squares it targets.
	 **/
	protected ArrayList<Move> generateMoves(Board board, Square square) {
		ArrayList<Move> targets = new ArrayList<>();
		
		int row = square.getRow();
		int col = square.getCol();
		Piece piece = square.getPiece();
		
		for (int[] dir : directions) {
			int newRow = row + dir[0];
			int newCol = col + dir[1];
			
			do {
				if (!board.isInBounds(newRow, newCol)) break;
				
				Square targetSquare = board.getSquare(newRow, newCol);
				Piece targetPiece = targetSquare.getPiece();
				
				Move move = new Move(row, col, newRow, newCol);
				move.pieceType = piece.getPieceType();
				
				if (targetPiece != null) {
					targets.add(move);
	                break;
	            }
				
				targets.add(move);
				newRow += dir[0];
				newCol += dir[1];
			} while (!singleMoves);
		}
		return targets;
	}
	
	// Setters and getters
	public void setHasMoved(boolean newValue) {
		this.hasMoved = newValue;
	}
	
	public boolean getHasMoved() {
		return this.hasMoved;
	}
	
	public Color getColor() {
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
		if (shortened == null) shortened = "";
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
	static PieceType getTypeFromShortened(char shortened) {
		// TODO
		return getTypeFromShortened(shortened + "");
	}
}

class Queen extends Piece {
	public Queen(Color color) {
		super(color);
		directions = new int[][] {
			{-1, 0},
			{1, 0},
			{0, -1},
			{0, 1},
			
			{-1, -1},
			{-1, 1},
			{1, -1},
			{1, 1}
		};
		singleMoves = false;
	}
	@Override
    public PieceType getPieceType() {
        return PieceType.QUEEN;
    }

    @Override
    public String getShortenedType() {
        return "Q";
    }
}

class Bishop extends Piece {
	public Bishop(Color color) {
		super(color);
		directions = new int[][] {
			{-1, -1},
			{-1, 1},
			{1, -1},
			{1, 1}
		};
		singleMoves = false;
	}
	@Override
	public PieceType getPieceType() {
	    return PieceType.BISHOP;
	}

	@Override
	public String getShortenedType() {
	    return "B";
	}
}

class Knight extends Piece {
	public Knight(Color color) {
		super(color);
		directions = new int[][] {
			{2, 1},
			{2, -1},
			{-2, 1},
			{-2, -1},
			{1, 2},
			{1, -2},
			{-1, 2},
			{-1, -2}
		};
		singleMoves = true;
	}
	@Override
	public PieceType getPieceType() {
	    return PieceType.KNIGHT;
	}

	@Override
	public String getShortenedType() {
	    return "N";
	}
	
	@Override
	protected ArrayList<Move> generateMoves(Board board, Square square) {
		// TODO
		ArrayList<Move> validMoves = new ArrayList<>();
		
		int row = square.getRow();
		int col = square.getCol();
		
		for (int[] offset : directions) {
			int newRow = row + offset[0];
			int newCol = col + offset[1];
			if (!board.isInBounds(newRow, newCol)) continue;
			
			Move move = new Move(row, col, newRow, newCol);
			move.pieceType = this.getPieceType();
			
			validMoves.add(move);
		}
		return validMoves;
	}
}

class Rook extends Piece {
	public Rook(Color color) {
		super(color);
		directions = new int[][] {
			{-1, 0},
			{1, 0},
			{0, -1},
			{0, 1},
		};
		singleMoves = false;
	}
	@Override
	public PieceType getPieceType() {
	    return PieceType.ROOK;
	}

	@Override
	public String getShortenedType() {
	    return "R";
	}
}

class Pawn extends Piece {
    private static final int[] ATTACK_OFFSETS = {-1, 1};

    public Pawn(Color color) {
        super(color);
    }

    @Override
    public PieceType getPieceType() {
        return PieceType.PAWN;
    }

    @Override
    public String getShortenedType() {
        return "";
    }

    private int getDirection() {
        return color == Color.WHITE ? -1 : 1;
    }

    private boolean isStartingRank(int row) {
        return (color == Color.WHITE && row == 6) ||
               (color == Color.BLACK && row == 1);
    }

    @Override
    public ArrayList<Move> getValidMoves(Board board, Square square) {
        ArrayList<Move> moves = new ArrayList<>();
        int row = square.getRow();
        int col = square.getCol();

        addForwardMoves(board, moves, row, col);

        addCaptureMoves(board, moves, row, col);

        addEnPassantMoves(board, moves, row, col);

        return moves;
    }

    private void addForwardMoves(Board board, ArrayList<Move> moves, int row, int col) {
        int forwardRow = row + getDirection();
        if (!board.isInBounds(forwardRow, col)) return;

        Square forwardSquare = board.getSquare(forwardRow, col);

        if (forwardSquare.getPiece() != null) return;

        Move move = new Move(row, col, forwardRow, col);
        move.pieceType = getPieceType();

        moves.add(move);

        // Double Move
        if (!isStartingRank(row)) return;
        int doubleRow = row + getDirection() * 2;        
        if (!board.isInBounds(doubleRow, col)) return;

        Square doubleSquare = board.getSquare(doubleRow, col);

        if (doubleSquare.getPiece() != null) return;

        Move doubleMove = new Move(row, col, doubleRow, col);
        doubleMove.pieceType = getPieceType();
        
        moves.add(doubleMove);
    }

    private void addCaptureMoves(Board board, ArrayList<Move> moves, int row, int col) {
        int targetRow = row + getDirection();
        for (int offset : ATTACK_OFFSETS) {
            int targetCol = col + offset;

            if (!board.isInBounds(targetRow, targetCol)) continue;

            Square targetSquare = board.getSquare(targetRow, targetCol);
            Piece targetPiece = targetSquare.getPiece();

            if (targetPiece == null || targetPiece.getColor() == this.color)continue;

            Move move = new Move(row, col, targetRow, targetCol);
            move.pieceType = getPieceType();
            move.isCapture = true;

            moves.add(move);
        }
    }

    private void addEnPassantMoves(Board board, ArrayList<Move> moves, int row, int col) {
        Move lastMove = board.getLastMove();

        if (lastMove == null || lastMove.pieceType != PieceType.PAWN) return;
        if (Math.abs(lastMove.fromRank - lastMove.toRank) != 2)return;

        Square enemySquare = board.getSquare(lastMove.toRank, lastMove.toFile);

        Piece enemyPawn = enemySquare.getPiece();

        if (enemyPawn == null || enemyPawn.getColor() == this.color)return;
        if (lastMove.toRank != row || Math.abs(lastMove.toFile - col) != 1) return;

        Move move = new Move(row, col, row + getDirection(), lastMove.toFile);

        move.pieceType = getPieceType();
        move.isCapture = true;
        move.isEnPassant = true;

        moves.add(move);
    }

    @Override
    public ArrayList<Move> getAttackingMap(Board board, Square square) {
        ArrayList<Move> attacks = new ArrayList<>();

        int row = square.getRow();
        int col = square.getCol();

        int targetRow = row + getDirection();

        for (int offset : ATTACK_OFFSETS) {
            int targetCol = col + offset;

            if (!board.isInBounds(targetRow, targetCol)) continue;

            Move move = new Move(row, col, targetRow, targetCol);

            move.pieceType = getPieceType();

            attacks.add(move);
        }

        return attacks;
    }
}

class King extends Piece {
	public King(Color color) {
		super(color);
		directions = new int[][] {
			{-1, -1},
			{-1, 0},
			{-1, 1},
			{0, -1},
			{0, 1},
			{1, -1},
			{1, 0},
			{1, 1}
		};
		singleMoves = true;
	}
	@Override
	public PieceType getPieceType() {
	    return PieceType.KING;
	}

	@Override
	public String getShortenedType() {
	    return "K";
	}
	
	@Override
	public ArrayList<Move> getValidMoves(Board board, Square square) {
	    ArrayList<Move> validMoves = super.getValidMoves(board, square);

	    // Castling
	    if (!hasMoved) {
	        int row = square.getRow();
	        
	        //Short
	        Square shortRookSquare = board.getSquare(row, 7);

	        if (shortRookSquare != null && shortRookSquare.getPiece() instanceof Rook) {
	            Piece rook = shortRookSquare.getPiece();
	            if (!rook.getHasMoved()) {
	                if (board.getSquare(row, 5).getPiece() == null && board.getSquare(row, 6).getPiece() == null) {
	                    Move move = new Move(row, 4, row, 6);
	                    move.pieceType = this.getPieceType();
	                    move.isShortCastle = true;

	                    validMoves.add(move);
	                }
	            }
	        }

	        // Long
	        Square longRookSquare = board.getSquare(row, 0);

	        if (longRookSquare != null && longRookSquare.getPiece() instanceof Rook) {
	            Piece rook = longRookSquare.getPiece();
	            
	            if (!rook.getHasMoved()) {
	                if (board.getSquare(row, 1).getPiece() == null &&
	                    board.getSquare(row, 2).getPiece() == null &&
	                    board.getSquare(row, 3).getPiece() == null
	                   ) {
	                    Move move = new Move(row, 4, row, 2);

	                    move.pieceType = this.getPieceType();
	                    move.isLongCastle = true;

	                    validMoves.add(move);
	                }
	            }
	        }
	    }

	    return validMoves;
	}
}
