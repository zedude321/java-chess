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
		
	}
	
	public Piece getPieceAtSquare(int[] pos) {
		
	}
	
	public void applyMove(Piece piece, int[] destination) throws InvalidMoveException {
		
	}
	
	public ArrayList<Square> getLegalMoves(Piece piece) {
		
	}
	
	public boolean isSquareUnderAttack(Square square) {
		
	}
	
	public boolean isInCheck() {
		
	}
	
	public boolean isCheckmate() {
		
	}
	
	public boolean isStalemate() {
		
	}
	
	public void turn() {
		
	}
}
