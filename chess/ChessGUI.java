package chess;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

/**
 * Graphical User Interface for the chess game using javax.swing.
 * Displays an 8x8 board of clickable tiles and handles player interaction
 * through a two-click selection system.
 *
 * Structure:
 *   JFrame (ChessGUI)
 *     - boardPanel (JPanel, GridLayout 8x8)  <- CENTER
 *         - tiles[row][col] (JButton x64)
 *     - statusLabel (JLabel)                 <- SOUTH
 */
public class ChessGUI extends JFrame {
	
	private static final int SIZE = 8;
	private static final java.awt.Color LIGHT   = new java.awt.Color(240, 217, 181); 
    private static final java.awt.Color DARK    = new java.awt.Color(181, 136,  99); 
    private static final java.awt.Color SELECT  = new java.awt.Color(186, 202,  68); 
    private static final java.awt.Color SPECIAL = new java.awt.Color(255, 215,   0); 
	
	private final JButton[][] tiles = new JButton[SIZE][SIZE];
	private final JLabel statusLabel = new JLabel("White's turn", SwingConstants.CENTER);

	private final Game game;
	private int[] selectedSquare = null;
	
	private static final String[] WHITE_SYMBOLS = {"♔", "♕", "♖", "♗", "♘", "♙"};
	private static final String[] BLACK_SYMBOLS = {"♚", "♛", "♜", "♝", "♞", "♟"};
	
	/**
	 * Constructs the chess GUI window and initializes all board components.
	 * Sets up the 8x8 grid of tiles, attaches action listeners, and displays
	 * the initial board state.
	 *
	 * @param game the Game instance containing board state and move logic
	 */
	public ChessGUI(Game game) {
		this.game = game;
		
		setTitle("Chess");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		JPanel boardPanel = new JPanel(new GridLayout(SIZE, SIZE));
		
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				JButton tile = new JButton();
				tile.setFont(new Font("Serif", Font.PLAIN, 38)); 
				tile.setFocusPainted(false);
				tile.setBorderPainted(false);
				tile.setOpaque(true);
				tile.setPreferredSize(new Dimension(80, 80));
				
				tile.setBackground(getDefaultColor(row, col));
				
				tile.setActionCommand(row + "," + col);
				tile.addActionListener(new TileClickListener());
				
				tiles[row][col] = tile;
				boardPanel.add(tile);
			}
		}
		
		statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		statusLabel.setPreferredSize(new Dimension(640, 40));
		
		Container c = getContentPane();
		c.add(boardPanel, BorderLayout.CENTER);
		c.add(statusLabel, BorderLayout.SOUTH);
		
		updateBoard();
		
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}
	
	/**
	 * Inner ActionListener class that handles tile click events.
	 * Implements a two-click system: the first click selects a piece,
	 * and the second click attempts to move it to the target square.
	 */
	class TileClickListener implements ActionListener {
		
		/**
		 * Responds to a tile being clicked.
		 * On the first click, selects the piece if it belongs to the current player.
		 * On the second click, attempts to move the selected piece to the clicked square.
		 *
		 * @param e the ActionEvent containing the clicked tile's row and column
		 */
		public void actionPerformed(ActionEvent e) {
			String[] parts = e.getActionCommand().split(",");
			int row = Integer.parseInt(parts[0]);
			int col = Integer.parseInt(parts[1]);
			
			if (selectedSquare == null) {
				Piece clickedPiece = game.board.getSquare(row, col).getPiece();
				
				if (clickedPiece == null) return;
				if (clickedPiece.getColor() != game.currentTurn) return;
				
				selectedSquare = new int[]{row, col};
				tiles[row][col].setBackground(SELECT);
			}
			else {
				int fromRow = selectedSquare[0];
				int fromCol = selectedSquare[1];
				
				resetTileColor(fromRow, fromCol);
				
				if (fromRow == row && fromCol == col) {
					selectedSquare = null;
					return;
				}
				
				Move move = buildMove(fromRow, fromCol, row, col);
				selectedSquare = null;
				
				if (move != null) {
					try {
						game.applyMove(move);
						updateBoard();
						updateStatus();
					} catch (InvalidMoveException ex) {
						statusLabel.setText("Invalid move! " + game.currentTurn + "'s turn");	
					}
				} else {
					statusLabel.setText("Invalid move! " + game.currentTurn + "'s turn");
				}
			}
		}
	}
	
	/**
	 * Attempts to build a valid Move from the source square to the target square.
	 * Validates the move against the piece's legal move list.
	 * Automatically assigns queen promotion if a pawn reaches the back rank.
	 *
	 * @param fromRow the row of the piece to move
	 * @param fromCol the column of the piece to move
	 * @param toRow   the destination row
	 * @param toCol   the destination column
	 * @return the matching Move if valid, or null if the move is not legal
	 */
	private Move buildMove(int fromRow, int fromCol, int toRow, int toCol) {
		Square fromSquare = game.board.getSquare(fromRow, fromCol);
		Piece piece = fromSquare.getPiece();
		if (piece == null) return null;
		
		ArrayList<Move> validMoves = piece.getValidMoves(game.board, fromSquare);
		
		for (Move m : validMoves) {
			if (m.toRank == toRow && m.toFile == toCol) {
				if (piece.getPieceType() == PieceType.PAWN) {
					if ((piece.getColor() == chess.Color.WHITE && toRow == 0) ||
						(piece.getColor() == chess.Color.BLACK && toRow == 7)) {
						m.promotedPiece = PieceType.QUEEN;
					}
				}
				return m;
			}
		}
		return null;
	}
	
	/**
	 * Reads the current board state from the Game and updates all tile labels
	 * and background colors to reflect the positions of all pieces.
	 * Highlights the special square in gold when it is empty.
	 */
	public void updateBoard() {
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				Square square = game.board.getSquare(row, col);
				Piece piece = square.getPiece();
				
				if (game.board.isSpecialSquare(row, col) && piece == null) {
					tiles[row][col].setBackground(SPECIAL);
				} else {
					tiles[row][col].setBackground(getDefaultColor(row, col));
				}
				
				if (piece == null) {
					tiles[row][col].setText("");
				} else {
					tiles[row][col].setText(getPieceSymbol(piece));
					if (piece.getColor() == Color.WHITE) {
						tiles[row][col].setForeground(java.awt.Color.DARK_GRAY);
					} else {
						tiles[row][col].setForeground(java.awt.Color.BLACK);
					}
				}
			}
		}
	}
	
	/**
	 * Updates the status label at the bottom of the window
	 * to show whose turn it currently is.
	 */
	private void updateStatus() {
		statusLabel.setText(game.currentTurn == chess.Color.WHITE ? "White's turn" : "Black's turn");
	}
	
	/**
	 * Returns the Unicode chess symbol for the given piece.
	 *
	 * @param p the Piece to get the symbol for
	 * @return a String containing the Unicode chess symbol
	 */
	private String getPieceSymbol(Piece p) {
		int idx;
		switch (p.getPieceType()) {
		case KING: 		idx = 0; break;
		case QUEEN: 	idx = 1; break;
		case ROOK: 		idx = 2; break;
		case BISHOP:	idx = 3; break;
		case KNIGHT:	idx = 4; break;
		default:		idx = 5; break;
		}
		return p.getColor() == chess.Color.WHITE ? WHITE_SYMBOLS[idx] : BLACK_SYMBOLS[idx];
	}
	
	/**
	 * Returns the default checkerboard background color for a tile
	 * based on its row and column position.
	 *
	 * @param row the row index of the tile
	 * @param col the column index of the tile
	 * @return the background Color for the tile
	 */
	private java.awt.Color getDefaultColor(int row, int col) {
		return (row + col) % 2 == 0 ? LIGHT : DARK;
	}
	
	/**
	 * Resets the background color of a tile to its default checkerboard color.
	 * Used to remove the selection highlight after a piece is deselected or moved.
	 *
	 * @param row the row index of the tile to reset
	 * @param col the column index of the tile to reset
	 */
	private void resetTileColor(int row, int col) {
		tiles[row][col].setBackground(getDefaultColor(row, col));
	}
}
