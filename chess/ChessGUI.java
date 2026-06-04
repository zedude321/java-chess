package chess;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;

/**
 * Graphical User Interface for the chess game using javax.swing. Displays an
 * 8x8 board of clickable tiles and handles player interaction through a
 * two-click selection system.
 *
 * Structure: JFrame (ChessGUI) - boardPanel (JPanel, GridLayout 8x8) <- CENTER
 * - tiles[row][col] (JButton x64) - statusLabel (JLabel) <- SOUTH
 */
public class ChessGUI extends JFrame {

    private static final long serialVersionUID = 1L;
	private static final int SIZE = 8;
    private static final java.awt.Color LIGHT = new java.awt.Color(240, 217, 181);
    private static final java.awt.Color DARK = new java.awt.Color(181, 136, 99);
    private static final java.awt.Color SELECT = new java.awt.Color(186, 202, 68);
    private static final java.awt.Color SPECIAL = new java.awt.Color(255, 215, 0);

    private final JButton[][] tiles = new JButton[SIZE][SIZE];
    private final JLabel statusLabel = new JLabel("White's turn", SwingConstants.CENTER);
    private final JButton saveButton = new JButton("Save Game");
    private final JButton loadButton = new JButton("Load Game");
    private final JTextField ipAddressInput = new JTextField();
    private final JLabel ipAddressLabel = new JLabel("", SwingConstants.CENTER);
    private final JToggleButton colorToggle = new JToggleButton("WHITE");
    private final CardLayout cardLayout;
    private final JPanel rootPanel;

    private JPanel menuPanel;
    private JPanel boardPanel;
    private JPanel hostServerPanel;
    private JPanel joinServerPanel;

    private Game game = new Game();
    private int[] selectedSquare = null;
    private ServerHandler server;
    private Color color = Color.WHITE;
    private boolean multiplayer;

    private static final String[] WHITE_SYMBOLS = {"♔", "♕", "♖", "♗", "♘", "♙"};
    private static final String[] BLACK_SYMBOLS = {"♚", "♛", "♜", "♝", "♞", "♟"};

    /**
     * Constructs the chess GUI window, the 4 Card panels and sets the default
     * panel to menu.
     */
    public ChessGUI() {
        setTitle("Chess");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        rootPanel = new JPanel(cardLayout);

        createMenuPanel();
        createBoardPanel();
        createHostServerPanel();
        createJoinServerPanel();

        rootPanel.add(hostServerPanel, "HOST");
        rootPanel.add(joinServerPanel, "JOIN");
        rootPanel.add(menuPanel, "MENU");
        rootPanel.add(boardPanel, "BOARD");

        add(rootPanel);

        cardLayout.show(rootPanel, "MENU");

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Creates the Menu Panel elements. Sets up action listeners to createServer
     * and client buttons
     */
    private void createMenuPanel() {
        menuPanel = new JPanel(new BorderLayout());

        JPanel buttons = new JPanel(new FlowLayout());

        JButton createServer = new JButton("Host Server");
        JButton client = new JButton("Join Server");
        JButton singleplayer = new JButton("Play Singleplayer");

        createServer.addActionListener(new CreateServer());
        client.addActionListener(e -> {
        	multiplayer = true;
            cardLayout.show(rootPanel, "JOIN");
        });
        singleplayer.addActionListener(new Singleplayer());

        buttons.add(createServer);
        buttons.add(client);
        buttons.add(singleplayer);

        menuPanel.add(ipAddressInput, BorderLayout.CENTER);
        menuPanel.add(buttons, BorderLayout.SOUTH);
    }

    /**
     * Initializes all board components. Sets up the 8x8 grid of tiles, attaches
     * action listeners, and displays the initial board state.
     */
    private void createBoardPanel() {
        boardPanel = new JPanel(new BorderLayout());

        JPanel grid = new JPanel(new GridLayout(SIZE, SIZE));

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {

                JButton tile = new JButton();

                tile.setFont(new Font("Serif", Font.PLAIN, 38));
                tile.setFocusPainted(false);
                tile.setBorderPainted(false);
                tile.setOpaque(true);
                tile.setPreferredSize(new Dimension(80, 80));

                tile.setActionCommand(row + "," + col);
                tile.addActionListener(new TileClickListener());

                tiles[row][col] = tile;
                grid.add(tile);
            }
        }
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		statusLabel.setPreferredSize(new Dimension(640, 40));
		
		saveButton.addActionListener(new SaveLoadListener("save"));
		loadButton.addActionListener(new SaveLoadListener("load"));
		
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(saveButton);
		buttonPanel.add(loadButton);
		boardPanel.add(buttonPanel, BorderLayout.NORTH);
        boardPanel.add(grid, BorderLayout.CENTER);
        boardPanel.add(statusLabel, BorderLayout.SOUTH);
    }

    /**
     * Constructs the host server panel adding an action listener to the button
     * and an item listener to toggle which color to start from.
     */
    private void createHostServerPanel() {
        hostServerPanel = new JPanel(new GridLayout(3, 1));

        JButton startServer = new JButton("Start Game");

        startServer.addActionListener(new StartServer());
        colorToggle.addItemListener(new ColorToggleListener());

        hostServerPanel.add(ipAddressLabel);
        hostServerPanel.add(colorToggle);
        hostServerPanel.add(startServer);
    }

    /**
     * Constructs the join server panel adding an action listener to the
     * joinButton.
     */
    private void createJoinServerPanel() {
        joinServerPanel = new JPanel(new GridLayout(2, 1));
        JButton joinButton = new JButton("Join Game");

        ipAddressInput.setHorizontalAlignment(JTextField.CENTER);

        joinButton.addActionListener(new JoinServer());

        joinServerPanel.add(ipAddressInput);
        joinServerPanel.add(joinButton);
    }

    /**
     * Disables all buttons in the host server panel. Starts the server in a
     * thread. After the server stops sets statusLabel to disconnected.
     */
    class StartServer implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton btn = (JButton) e.getSource();
            colorToggle.setEnabled(false);
            btn.setEnabled(false);
            btn.setText("Waiting for opponent");

            new Thread(() -> {
                ((Server) server).startServer(new Listener(), color, game);
                if (game.getGameState() == null) {
                    statusLabel.setText("Disconnected");
                } else {
                    statusLabel.setText("Disconnected: " + game.getGameState().toString());
                }
                server = null;
            }).start();
        }
    }

    /**
     * Creates a new server instance and switches to the HOST panel, whilst
     * setting the ipAddress
     */
    class CreateServer implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
        	multiplayer = true;
            server = new Server(3000);

            ipAddressLabel.setText(((Server) server).getIPAddress());
            cardLayout.show(rootPanel, "HOST");
        }
    }

    /**
     * Creates a new server instance of Client and joins the server in a thread.
     * Once the server ends sets statusLabel.
     */
    class JoinServer implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            server = new Client(3000);

            new Thread(() -> {
                ((Client) server).joinServer(ipAddressInput.getText(), new Listener(), game);

                SwingUtilities.invokeLater(() -> {
                    if (game.getGameState() == null) {
                        statusLabel.setText("Disconnected");
                    } else {
                        statusLabel.setText("Disconnected: " + game.getGameState().toString());
                    }
                });
                server = null;
            }).start();
        }
    }
    
    /**
     * Starts a singleplayer game of chess.
     */
    class Singleplayer implements ActionListener {
    	@Override
        public void actionPerformed(ActionEvent e) {
    		multiplayer = false;
    		game.initializeGame();
    		updateBoard();
            updateStatus();

            rootPanel.revalidate();
            rootPanel.repaint();
    		cardLayout.show(rootPanel, "BOARD");
    	}
    }

    /**
     * When ColorToggle is toggled sets the color property.
     */
    class ColorToggleListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                colorToggle.setText("WHITE");
                color = Color.WHITE;
            } else {
                colorToggle.setText("BLACK");
                color = Color.BLACK;
            }
        }
    }

    /**
     * A class that is sent to the server, it updates the board update() is
     * called. Useful for when moves are done in the server.
     */
    class Listener implements GameReadyListener {

        public void update() {
            SwingUtilities.invokeLater(() -> {
                updateBoard();
                updateStatus();

                rootPanel.revalidate();
                rootPanel.repaint();
            });
        }

        public void start(Color color) {
            SwingUtilities.invokeLater(() -> {
            	loadButton.setEnabled(false);
                cardLayout.show(rootPanel, "BOARD");
                setColor(color);
            });
        }
    }

    /**
     * Inner ActionListener class that handles tile click events. Implements a
     * two-click system: the first click selects a piece, and the second click
     * attempts to move it to the target square.
     */
    class TileClickListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (server == null && multiplayer) {
                return;
            }

            String[] parts = e.getActionCommand().split(",");
            int guiRow = Integer.parseInt(parts[0]);
            int guiCol = Integer.parseInt(parts[1]);

            int row = boardCoord(guiRow);
            int col = boardCoord(guiCol);

            if (selectedSquare == null) {
                Piece clickedPiece = game.board.getSquare(row, col).getPiece();
                if (clickedPiece == null) {
                    return;
                }
                if (clickedPiece.getColor() != game.currentTurn) return;
                if (multiplayer && clickedPiece.getColor() != color) return;

                selectedSquare = new int[]{guiRow, guiCol};

                tiles[guiRow][guiCol].setBackground(SELECT);

            } else {

                int fromGuiRow = selectedSquare[0];
                int fromGuiCol = selectedSquare[1];

                resetTileColor(fromGuiRow, fromGuiCol);

                if (fromGuiRow == guiRow && fromGuiCol == guiCol) {
                    selectedSquare = null;
                    return;
                }

                Move move = buildMove(
                        fromGuiRow,
                        fromGuiCol,
                        guiRow,
                        guiCol
                );

                selectedSquare = null;

                if (move != null) {
                	if (multiplayer) {
                		server.submitMove(move.toNotation());                		
                	} else {
                		try {
							game.applyMove(move);
							updateBoard();
							updateStatus();
						} catch (InvalidMoveException e1) {
							statusLabel.setText(
									"Invalid move! " + game.currentTurn + "'s turn"
		                    );
						}
                	}
                } else {
                    statusLabel.setText(
                            "Invalid move! " + game.currentTurn + "'s turn"
                    );
                }
            }
        }
    }

    /**
     * Inner ActionListener for Save and Load button. Opens a JFileChooser so
     * the player can pick a .txt file.
     */
    class SaveLoadListener implements ActionListener {

        private final String action;

        public SaveLoadListener(String action) {
            this.action = action;
        }

        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser(GameFileManager.getSavesFolder());
            fileChooser.setDialogTitle(action.equals("save") ? "Save Game" : "Load Game");

            int result = action.equals("save")
                    ? fileChooser.showSaveDialog(ChessGUI.this)
                    : fileChooser.showOpenDialog(ChessGUI.this);

            if (result == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getAbsolutePath().trim();
                if (!filename.endsWith(".txt")) {
                    filename += ".txt";
                }

                try {
                    if (action.equals("save")) {
                        GameFileManager.saveGame(game, filename);
                        statusLabel.setText("Game saved!");
                    } else {
                        GameFileManager.loadGame(game, filename);
                        updateBoard();
                        updateStatus();
                        statusLabel.setText("Game loaded!");
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(
                            ChessGUI.this, "File error: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
    }

    /**
     * Attempts to build a valid Move from the source square to the target
     * square. Validates the move against the piece's legal move list.
     * Automatically assigns queen promotion if a pawn reaches the back rank.
     *
     * @param fromRow the row of the piece to move
     * @param fromCol the column of the piece to move
     * @param toRow the destination row
     * @param toCol the destination column
     * @return the matching Move if valid, or null if the move is not legal
     */
    private Move buildMove(int fromGuiRow, int fromGuiCol, int toGuiRow, int toGuiCol) {

        int fromRow = boardCoord(fromGuiRow);
        int fromCol = boardCoord(fromGuiCol);
        int toRow = boardCoord(toGuiRow);
        int toCol = boardCoord(toGuiCol);

        Square fromSquare = game.board.getSquare(fromRow, fromCol);
        Piece piece = fromSquare.getPiece();

        if (piece == null) {
            return null;
        }

        ArrayList<Move> validMoves = piece.getValidMoves(game.board, fromSquare);

        for (Move m : validMoves) {
            if (m.toRank == toRow && m.toFile == toCol) {
                if (piece.getPieceType() == PieceType.PAWN) {
                    if ((piece.getColor() == Color.WHITE && toRow == 0) || (piece.getColor() == Color.BLACK && toRow == 7)) {
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
     * and background colors to reflect the positions of all pieces. Highlights
     * the special square in gold when it is empty.
     */
    public void updateBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int boardRow = boardCoord(row);
                int boardCol = boardCoord(col);

                Square square = game.board.getSquare(boardRow, boardCol);
                Piece piece = square.getPiece();

                if (game.board.isSpecialSquare(boardRow, boardCol) && piece == null) {
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
     * Gets the board coordinates from the gui coordinates. Flips the board when
     * color is black.
     *
     * @param guiRow the original coordinate in the gui.
     * @return the coordinates relative to the board.
     */
    private int boardCoord(int guiRow) {
        return color == Color.BLACK ? SIZE - 1 - guiRow : guiRow;
    }

    /**
     * Updates the status label at the bottom of the window to show whose turn
     * it currently is.
     */
    private void updateStatus() {
    	if (!multiplayer && game.getGameState() != null) {
    		statusLabel.setText(game.getGameState().toString());
    	} else {
    		statusLabel.setText(game.currentTurn == chess.Color.WHITE ? "White's turn" : "Black's turn");    		
    	}
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
            case KING: idx = 0; break;
            case QUEEN: idx = 1; break;
            case ROOK: idx = 2; break;
            case BISHOP: idx = 3; break;
            case KNIGHT: idx = 4; break;
            default: idx = 5; break;
        }
        return p.getColor() == chess.Color.WHITE ? WHITE_SYMBOLS[idx] : BLACK_SYMBOLS[idx];
    }

    /**
     * Returns the default checkerboard background color for a tile based on its
     * row and column position.
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
     * Used to remove the selection highlight after a piece is deselected or
     * moved.
     *
     * @param row the row index of the tile to reset
     * @param col the column index of the tile to reset
     */
    private void resetTileColor(int row, int col) {
        tiles[row][col].setBackground(getDefaultColor(row, col));
    }

    /**
     * Sets the current color property
     *
     * @param c the color to set
     */
    private void setColor(Color c) {
        this.color = c;
    }
}
