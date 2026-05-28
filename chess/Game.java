package chess;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The chess board
 *
 * @param currentTurn info of who's turn it is currently
 * @param board holds all squares of the board
 * @param turnHistory info of turn history
 * @param enPassantTarget Useful for performing and checking for en passant
 * @param halfMoveCounter Useful for checking for draws
 * @param isGameOver switches to true when game is over
 **/
public class Game {
	public Color currentTurn;
	public Board board;
	private ArrayList<Move> turnHistory;
	private int halfMoveCounter;
	private boolean isGameOver;
	private final boolean DEBUG_MODE = false;
	
	public void initializeGame() {
		board = new Board();
		
		currentTurn = Color.WHITE;
		turnHistory = new ArrayList<>();
		halfMoveCounter = 0;
		isGameOver = false;
	}
	
	public void applyMove(Move move) throws InvalidMoveException {
		makeMove(move);
		
		if (move.isShortCastle || move.isLongCastle) {
		    int middleFile = move.isShortCastle ? 5 : 3;

		    boolean illegal = isSquareUnderAttack(board.getSquare(move.fromRank, middleFile), currentTurn);

		    if (illegal) {
		        undoMove(move);
		        throw new InvalidMoveException("Illegal castle");
		    }
		}
		
		if (isInCheck(currentTurn)) {
			undoMove(move);
			throw new InvalidMoveException("Illegal Move: King in check");
		}
		
		// Check if piece landed on special square and transform it
		if (board.isSpecialSquare(move.toRank, move.toFile)) {
			transformPieceOnSpecialSquare(move.toRank, move.toFile, currentTurn);
		}
		
		// Update move
		board.setLastMove(move);
		turnHistory.add(move);
		
		if (move.pieceType == PieceType.PAWN || move.capturedPiece != null ) {
			halfMoveCounter = 0;
		} else {
			halfMoveCounter++;
		}
		
		if (currentTurn == Color.WHITE)	
			currentTurn = Color.BLACK;
	    else 
	    	currentTurn = Color.WHITE;
		
		updateGameState();
	}
	
	public boolean turn(Scanner sc) {
		this.printBoard();
		
		String notation = sc.nextLine();
		
		if (notation.equalsIgnoreCase("exit")) {
			sc.close();
			return false;
		}
		
		Move move = Move.parse(notation);
		
		if (move.isShortCastle) {
			int row = currentTurn == Color.WHITE ? 7 : 0;
			
			move.fromFile = 4;
			move.toFile = 6;
			move.fromRank = row;
			move.toRank = row;
		} else if (move.isLongCastle) {
			int row = currentTurn == Color.WHITE ? 7 : 0;
			
			move.fromFile = 4;
			move.toFile = 2;
			move.fromRank = row;
			move.toRank = row;
		}
		
		try {
			if (isGameOver) throw new InvalidMoveException("Game is over");
			
			Move m = this.findSquareFromNotation(move);
			if (m == null) throw new InvalidMoveException("Invalid move");
			
			move.fromFile = m.fromFile;
			move.fromRank = m.fromRank;
			move.isEnPassant = m.isEnPassant;
			move.isCapture = m.isCapture;
			move.pieceType = m.pieceType;
			applyMove(move);
		} catch (InvalidMoveException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * Helper functions
	 */
	
	public void printBoard() {
		for(int r = 0; r < 8; r++) {
			if (DEBUG_MODE) {
				System.out.print(r + " ");
			} else {
				System.out.print(8 - r + " ");
			}
			for(int c = 0; c < 8; c++) {
				char toPrint = '□';
				if (board.getSquare(r, c).getPiece() == null) {
					if (board.isSpecialSquare(r, c)) {
						toPrint = '✦';
					} else if (Square.getColor(r, c) == Color.BLACK) {
						toPrint = '▩';
					}
				} else {
					toPrint = __getPieceSymbol(board.getSquare(r, c).getPiece());
				}
				System.out.print(toPrint + " ");
			}
			System.out.println();
		}
		if (DEBUG_MODE) {
			System.out.println("# 0 1 2 3 4 5 6 7");
		} else {
			System.out.println("# a b c d e f g h");			
		}
	}
	
	private void updateGameState() {
		if (cantMove(currentTurn)) {
			if (isInCheck(currentTurn)) {
				// Checkmate for other player
			} else {
				// Stalemate
			}	
			isGameOver = true;
			return;
		}
		// TODO: Add Repetition checker
		if (halfMoveCounter >= 100) {
			isGameOver = true;
			// Draw
			return;
		}
	}
	
	private Move findSquareFromNotation(Move move) {
		for(int r = 0; r < 8; r++) {
			for(int c = 0; c < 8; c++) {
				Piece temp = board.getSquare(r, c).getPiece();
				
				if (temp == null) continue;
				
				if (temp.color != this.currentTurn) continue;
				
				if (temp.getPieceType() != move.pieceType) continue;
				
				for(Move m : temp.getValidMoves(board, board.getSquare(r, c))) {
					if (m.toFile == move.toFile && m.toRank == move.toRank) {
						if (move.fromFile >= 0 && move.fromFile != m.fromFile) continue;
						if (move.fromRank >= 0 && move.fromRank != m.fromRank) continue;
						return m;
					}
				}
			}
		}
		
		return null;
	}
	
	private boolean legalMoveExists(Color color) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				Piece p = board.getSquare(i, j).getPiece();
				if (p == null) continue;
				
				if (p.getColor() != color) continue;
				
				ArrayList<Move> pseudoLegalMoves = p.getValidMoves(board, board.getSquare(i, j));
				
				for(Move m : pseudoLegalMoves) {
					try {
						makeMove(m);
					} catch (InvalidMoveException e) {
						continue;
					}
					
					if (!isInCheck(color)) {
						undoMove(m);
						return true;
					}
					undoMove(m);
				}
			}
		}
		
		return false;
	}
	
	private void makeMove(Move move) throws InvalidMoveException {
		Square currSquare = board.getSquare(move.fromRank, move.fromFile); 
		Square destSquare = board.getSquare(move.toRank, move.toFile);
		
		if (currSquare == null || destSquare == null) throw new InvalidMoveException("Invalid square");
		
		Piece currPiece = currSquare.getPiece();
		
		if (currPiece == null) throw new InvalidMoveException("Invalid move: Piece doesn't exist");
		
		move.capturedPiece = destSquare.getPiece();
		move.movedPiece = currPiece;
		move.wasFirstMove = currPiece.getHasMoved();
		
		if (move.isEnPassant) {
	        int capturedRow = currPiece.getColor() == Color.WHITE ? move.toRank + 1 : move.toRank - 1;

	        Square capturedSquare = board.getSquare(capturedRow, move.toFile);

	        move.capturedPiece = capturedSquare.getPiece();

	        capturedSquare.setPiece(null);
	    }
		
		currSquare.setPiece(null);
		destSquare.setPiece(currPiece);
		currPiece.setHasMoved(true);
		
		// Promotion logic
		if (move.promotedPiece != null) {
			destSquare.setPiece(handlePromote(move));
		}
		
		// Castling
		if (move.isShortCastle) {
		    Square rookStart = board.getSquare(move.fromRank, 7);
		    Square rookEnd = board.getSquare(move.fromRank, 5);
		    Piece rook = rookStart.getPiece();

		    rookStart.setPiece(null);
		    rookEnd.setPiece(rook);
		    rook.setHasMoved(true);
		}

		if (move.isLongCastle) {
		    Square rookStart = board.getSquare(move.fromRank, 0);
		    Square rookEnd = board.getSquare(move.fromRank, 3);
		    Piece rook = rookStart.getPiece();

		    rookStart.setPiece(null);
		    rookEnd.setPiece(rook);
		    rook.setHasMoved(true);
		}
	}
	
	private void undoMove(Move move) {
		Square originalSquare = board.getSquare(move.fromRank, move.fromFile); 
		Square destSquare = board.getSquare(move.toRank, move.toFile);
		
		Piece originalPiece = move.promotedPiece != null ? move.movedPiece : destSquare.getPiece();
		
		originalSquare.setPiece(originalPiece);
		originalPiece.setHasMoved(move.wasFirstMove);
		
		if (move.isEnPassant) {
	        destSquare.setPiece(null);

	        int capturedRow = originalPiece.getColor() == Color.WHITE ? move.toRank + 1 : move.toRank - 1;
	        Square capturedSquare = board.getSquare(capturedRow, move.toFile);

	        capturedSquare.setPiece(move.capturedPiece);
	    } else {
	        destSquare.setPiece(move.capturedPiece);
	    }
		
		// Castling
		if (move.isShortCastle) {
		    Square rookStart = board.getSquare(move.fromRank, 7);
		    Square rookEnd = board.getSquare(move.fromRank, 5);
		    Piece rook = rookEnd.getPiece();

		    rookEnd.setPiece(null);
		    rookStart.setPiece(rook);
		    rook.setHasMoved(false);
		}

		if (move.isLongCastle) {
		    Square rookStart = board.getSquare(move.fromRank, 0);
		    Square rookEnd = board.getSquare(move.fromRank, 3);
		    Piece rook = rookEnd.getPiece();

		    rookEnd.setPiece(null);
		    rookStart.setPiece(rook);
		    rook.setHasMoved(false);
		}
	}
	
	private Piece handlePromote(Move move) {
		Piece promoted;
		switch(move.promotedPiece) {
			case PieceType.QUEEN: promoted = new Queen(move.movedPiece.color); break;
			case PieceType.ROOK: promoted = new Rook(move.movedPiece.color); break;
			case PieceType.BISHOP: promoted = new Bishop(move.movedPiece.color); break;
			case PieceType.KNIGHT: promoted = new Knight(move.movedPiece.color); break;
		default: promoted = null; break;
		}
		return promoted;
	}
	
	private void transformPieceOnSpecialSquare(int row, int col, Color playerColor) {
		Square specialSquare = board.getSquare(row, col);
		Piece piece = specialSquare.getPiece();
		
		if (piece == null || piece.getPieceType() == PieceType.KING) {
			return;
		}
		
		PieceType[] possibleTypes = {PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT, PieceType.PAWN};
		PieceType newType = possibleTypes[(int) (Math.random() * possibleTypes.length)];
		
		Piece newPiece = null;
		switch(newType) {
			case PieceType.QUEEN: newPiece = new Queen(piece.getColor()); break;
			case PieceType.ROOK: newPiece = new Rook(piece.getColor()); break;
			case PieceType.BISHOP: newPiece = new Bishop(piece.getColor()); break;
			case PieceType.KNIGHT: newPiece = new Knight(piece.getColor()); break;
			case PieceType.PAWN: newPiece = new Pawn(piece.getColor()); break;
			default: break;
		}
		
		if (newPiece != null) {
			newPiece.setHasMoved(piece.getHasMoved());
			specialSquare.setPiece(newPiece);
			System.out.println("✦ Piece transformed to " + newType + "!");
		}
	}
	
	private boolean isSquareUnderAttack(Square square, Color color) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				Piece piece = board.getSquare(i, j).getPiece();
				if (piece == null) {
					continue;
				}
				if (piece.getColor() == color) {
					continue;
				}
				
				ArrayList<Move> attackingMoves = piece.getAttackingMap(board, board.getSquare(i, j));
				
				for(Move m : attackingMoves) {
					if (m.toFile == square.col && m.toRank == square.row) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private boolean cantMove(Color color) {
		return !legalMoveExists(color);
	}
	
	private boolean isInCheck(Color color) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				Piece piece = board.getSquare(i, j).getPiece();
				if (piece == null) {
					continue;
				}
				if (piece.getColor() != color) {
					continue;
				}
				if (piece.getPieceType() == PieceType.KING) {
					return isSquareUnderAttack(board.getSquare(i, j), color);
				}
			}
		}
		return false;
	}
	
	private char __getPieceSymbol(Piece p) {
		switch(p.getPieceType()) {
			case PieceType.KING: 
				return p.getColor() == Color.WHITE ? '♔' : '♚';
			case PieceType.QUEEN:
				return p.getColor() == Color.WHITE ? '♕' : '♛';
			case PieceType.ROOK: 
				return p.getColor() == Color.WHITE ? '♖' : '♜';
			case PieceType.BISHOP: 
				return p.getColor() == Color.WHITE ? '♗' : '♝';
			case PieceType.KNIGHT: 
				return p.getColor() == Color.WHITE ? '♘' : '♞';
			case PieceType.PAWN: 
				return p.getColor() == Color.WHITE ? '♙' : '♟';
		}
		return 0;
	}
}

class Board {
	public Square[][] squares;
	private Move lastMove;
	private int specialRow;
	private int specialCol;
	
	public Board() {
        squares = new Square[8][8];
        lastMove = null;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col] = new Square(row, col);
            }
        }
        
        // Generate random special square between rows 3-6
        specialRow = 3 + (int) (Math.random() * 4);
        specialCol = (int) (Math.random() * 8);
        
        for (int col = 0; col < 8; col++) {
            getSquare(1, col).setPiece(new Pawn(Color.BLACK));
            getSquare(6, col).setPiece(new Pawn(Color.WHITE));
        }

        // Rooks
        getSquare(0, 0).setPiece(new Rook(Color.BLACK));
        getSquare(0, 7).setPiece(new Rook(Color.BLACK));

        getSquare(7, 0).setPiece(new Rook(Color.WHITE));
        getSquare(7, 7).setPiece(new Rook(Color.WHITE));

        // Knights
        getSquare(0, 1).setPiece(new Knight(Color.BLACK));
        getSquare(0, 6).setPiece(new Knight(Color.BLACK));

        getSquare(7, 1).setPiece(new Knight(Color.WHITE));
        getSquare(7, 6).setPiece(new Knight(Color.WHITE));

        // Bishops
        getSquare(0, 2).setPiece(new Bishop(Color.BLACK));
        getSquare(0, 5).setPiece(new Bishop(Color.BLACK));

        getSquare(7, 2).setPiece(new Bishop(Color.WHITE));
        getSquare(7, 5).setPiece(new Bishop(Color.WHITE));

        // Queens
        getSquare(0, 3).setPiece(new Queen(Color.BLACK));
        getSquare(7, 3).setPiece(new Queen(Color.WHITE));

        // Kings
        getSquare(0, 4).setPiece(new King(Color.BLACK));
        getSquare(7, 4).setPiece(new King(Color.WHITE));
    }
	
	public Square getSquare(int row, int col) {
        if (!isInBounds(row, col)) {
            return null;
        }

        return squares[row][col];
    }
	
	public boolean isInBounds(int row, int col) {
        return row >= 0 && row < 8 &&
               col >= 0 && col < 8;
    }
	
	// Setter and getter
	public Move getLastMove() {
		return this.lastMove;
	}
	
	public void setLastMove(Move m) {
		this.lastMove = m;
	}
	
	public boolean isSpecialSquare(int row, int col) {
		return row == specialRow && col == specialCol;
	}
	
	public int getSpecialRow() {
		return specialRow;
	}
	
	public int getSpecialCol() {
		return specialCol;
	}

}
