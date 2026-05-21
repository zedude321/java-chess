package chess;
import java.util.ArrayList;

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
public class Board {
	public Color currentTurn;
	private Square[][] board;
	private ArrayList<String> turnHistory;
	private Square enPassantTarget; 
	private int halfMoveCounter;
	private boolean isGameOver;
	
	public void initializeBoard() {
		
		board = new Square[8][8];
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				board[i][j] = new Square();
				board[i][j].setLocation(new int[]{i, j});
			}
		}
		
		board[0][0].setPiece(new Rook(board[0][0], Color.BLACK));
		board[0][1].setPiece(new Knight(board[0][1], Color.BLACK));
		board[0][2].setPiece(new Bishop(board[0][2], Color.BLACK));
		board[0][3].setPiece(new Queen(board[0][3], Color.BLACK));
		board[0][4].setPiece(new King(board[0][4], Color.BLACK));
		board[0][5].setPiece(new Bishop(board[0][5], Color.BLACK));
		board[0][6].setPiece(new Knight(board[0][6], Color.BLACK));
		board[0][7].setPiece(new Rook(board[0][7], Color.BLACK));
		
		for (int j = 0; j < 8; j++) {
			board[1][j].setPiece(new Pawn(board[1][j], Color.BLACK));
		}
		
		board[7][0].setPiece(new Rook(board[7][0], Color.WHITE));
		board[7][1].setPiece(new Knight(board[7][1], Color.WHITE));
		board[7][2].setPiece(new Bishop(board[7][2], Color.WHITE));
		board[7][3].setPiece(new Queen(board[7][3], Color.WHITE));
		board[7][4].setPiece(new King(board[7][4], Color.WHITE));
		board[7][5].setPiece(new Bishop(board[7][5], Color.WHITE));
		board[7][6].setPiece(new Knight(board[7][6], Color.WHITE));
		board[7][7].setPiece(new Rook(board[7][7], Color.WHITE));
		
		for (int j = 0; j < 8; j++) {
			board[6][j].setPiece(new Pawn(board[6][j], Color.WHITE));
		}
		
		currentTurn = Color.WHITE;
		turnHistory = new ArrayList<>();
		enPassantTarget = null;
		halfMoveCounter = 0;
		isGameOver = false;
	}
	
	public Piece getPieceAtSquare(int[] pos) {
		
		return board[pos[0]][pos[1]].getPiece();
	
	}
	
	public void applyMove(Piece piece, int[] destination) throws InvalidMoveException {

		if (isGameOver) {
			throw new InvalidMoveException("Game is over");
		}
		if (piece.getColor() != currentTurn) {
			throw new InvalidMoveException("Not your turn");
		}

		Square currSquare = piece.getSquare();
		Square destSquare = board[destination[0]][destination[1]];
		
		if (!isLegalMove(piece, destSquare)) {
			throw new InvalidMoveException("Invalid move");
		}
		
		handleEnPassant(piece, destination);
		
		Piece targetPiece = destSquare.getPiece();
		
		currSquare.setPiece(null);
		destSquare.setPiece(piece);
		piece.setSquare(destSquare);
		piece.setHasMoved(true);
		
		if (piece.getPieceType() == PieceType.PAWN ||
			targetPiece != null ) {
			halfMoveCounter = 0;
		} else {
			halfMoveCounter++;
		}
		
		enPassantTarget = null;
		if (piece.getPieceType() == PieceType.PAWN) {
			int startRow = currSquare.getLocation()[0];
			int endRow = destSquare.getLocation()[0];
			
			if (Math.abs(startRow - endRow) == 2) {
				int middleRow = (startRow + endRow) / 2;
				enPassantTarget = board[middleRow][destination[1]];
			}
		}
		
		turnHistory.add(piece.getShortenedType() + destination[0] + destination[1]);
		int row = destination[0];

		if ((piece.getColor() == Color.WHITE && row == 0) ||
		    (piece.getColor() == Color.BLACK && row == 7)) {

		    ((Pawn) piece).promote();
		}
		turn();
		updateGameState();
	}
	
	private void handleEnPassant(Piece piece, int[] destination) {
		Square destSquare = board[destination[0]][destination[1]];
		Piece targetPiece = destSquare.getPiece();
		if (piece.getPieceType() == PieceType.PAWN) {
			if (destSquare == enPassantTarget &&
				targetPiece == null) {
				int direction;
				if (piece.getColor() == Color.WHITE) {
					direction = 1;
				} else {
					direction = -1;
				}
				int targetRow = destination[0] + direction;
				int targetCol = destination[1];
				board[targetRow][targetCol].setPiece(null);
			}
		}
	}
	
	public ArrayList<Square> getLegalMoves(Piece piece) {
		
		ArrayList<Square> legalMoves = new ArrayList<>();
		ArrayList<Square> validMoves = piece.getValidMoves(this);
		Square initialSquare = piece.getSquare();
		for (Square targetSquare : validMoves) {
			Piece targetPiece = targetSquare.getPiece();
			if (targetPiece != null && 
				targetPiece.getColor() == piece.getColor()) {
				continue;
			}
			
			initialSquare.setPiece(null);
			targetSquare.setPiece(piece);
			piece.setSquare(targetSquare);
			if (targetPiece != null) {
				targetPiece.setSquare(null);
			}
			boolean inCheck = isInCheck(piece.getColor());
			targetSquare.setPiece(targetPiece);
			initialSquare.setPiece(piece);
			piece.setSquare(initialSquare);
			if (targetPiece != null) {
				targetPiece.setSquare(targetSquare);
			}
			
			if (!inCheck) {
				legalMoves.add(targetSquare);
			}
		}
		return legalMoves;
	}
	
	public boolean isLegalMove(Piece piece, Square destination) {
		return getLegalMoves(piece).contains(destination);
	}
	
	public boolean isSquareUnderAttack(Square square, Color color) {
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				Piece piece = board[i][j].getPiece();
				if (piece == null) {
					continue;
				}
				if (piece.getColor() == color) {
					continue;
				}
				
				ArrayList<Square> attackingMoves = piece.getAttackingMoves(this);
				
				if (attackingMoves.contains(square)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isInCheck(Color color) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				Piece piece = board[i][j].getPiece();
				if (piece == null) {
					continue;
				}
				if (piece.getColor() != color) {
					continue;
				}
				if (piece.getPieceType() == PieceType.KING) {
					return isSquareUnderAttack(board[i][j], color);
				}
			}
		}
		return false;
	}
	
	public boolean isCheckmate() {
		if (!isInCheck(currentTurn)) {
			return false;
		}
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				Piece piece = board[i][j].getPiece();
				if (piece == null) {
					continue;
				}
				if (piece.getColor() != currentTurn) {
					continue;
				}
				if (!getLegalMoves(piece).isEmpty()) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean isStalemate() {
		if (isInCheck(currentTurn)) {
			return false;
		}
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				Piece piece = board[i][j].getPiece();
				if (piece == null) {
					continue;
				}
				if (piece.getColor() != currentTurn) {
					continue;
				}
				if (!getLegalMoves(piece).isEmpty()) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void turn() {
		if (currentTurn == Color.WHITE)	
			currentTurn = Color.BLACK;
	    else 
	    	currentTurn = Color.WHITE;
	}
	public void updateGameState() {
		if (isCheckmate() || isStalemate()) {
			isGameOver = true;
		}
		if (halfMoveCounter >= 100) {
			isGameOver = true;
		}
	}
	
	public Square getSquare(int row, int col) {
		return board[row][col];
	}
	
	public Square getEnPassantTarget() {
		return enPassantTarget;
	}
}
