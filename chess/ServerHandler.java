package chess;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * An interface acts as a listener to the ChessGUI object to call when game is updated.
 */
interface GameReadyListener {
    public void update();
    public void start(Color color);
}

/**
 * The abstract class of Server and Client. 
 */
abstract public class ServerHandler {
	protected final int port;
	protected final BlockingQueue<String> moves = new LinkedBlockingQueue<>();
	
	protected Color color;
	
	public ServerHandler(int port) {
		this.port = port;
	}
	
	public void submitMove(String move) {
	    moves.offer(move);
	}
	
	public Color getColor() {
		return this.color;
	}
}

/**
 * The server object. when startServer is called creates a socket server.
 */
class Server extends ServerHandler {
	private String ipAddress;
	
	/**
	 * Sets the port to run on, and sets the address the socket server is run on.
	 * 
	 * @param port the port to start the server on
	 */
	public Server(int port) {
		super(port);
		
		InetAddress localHost = null;
		try {
			localHost = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		ipAddress = localHost.getHostAddress();
		
		System.out.println("IP: " + ipAddress);
	}
	
	/**
	 * Creates a socket server that only allows turns based on current color and sends 
	 * receives moves via sockets and the thread-safe BlockingQueue.
	 * 
	 * Sends the game information like color the special square and the random seed to client
	 * Alternates between getting moves from BlockingQueue and sending them via sockets.
	 * 
	 * @param listener the listener to send updates to
	 * @param c the chess color of the host
	 * @param game the game object
	 */
	public void startServer(GameReadyListener listener, Color c, Game game) {
		this.color = c;
        System.out.println("Server started.");
        System.out.println("Waiting for a client...");

        try (
            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket = serverSocket.accept();
            Scanner scanner = new Scanner(System.in);
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
    		DataInputStream input = new DataInputStream(socket.getInputStream())
        ) {
        	game.initializeGame();
        	
	        listener.start(color);
	        listener.update();
            System.out.println("Client connected.");
            
            output.writeUTF(color.toString());
            output.write(game.getSpecialRow());
            output.write(game.getSpecialCol());
            output.writeUTF(game.getRandomSeed() + "");

            while (game.getGameState() == null) {
            	String move;
            	if (game.currentTurn == color) {
            		move = moves.take();
                    output.writeUTF(move);
                    output.flush();
            	} else {
            		move = input.readUTF();
            		System.out.println("Message from server: " + move);
            	}
            	
            	if (move.equals("exit")) {
            		System.out.println("Closed the connection");
            		break;
            	}
            	
            	try {
            		game.turn(move);
        	        listener.update();
            	} catch (Exception e) {
					System.out.println(e.getMessage());
				}
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        } catch (InterruptedException e) {
        	System.out.println("Server error(Interrupted): " + e.getMessage());
		}
	}
	
	public String getIPAddress() {
		return this.ipAddress;
	}
}

/**
 * The client object. when joinServer is called joins a socket server.
 */
class Client extends ServerHandler {
	public Client(int port) {
		super(port);
	}
	
	/**
	 * Joins a socket server that only allows turns based on current color and sends 
	 * receives moves via sockets and the thread-safe BlockingQueue.
	 * 
	 * Receives the game information like color the special square and the random seed from server.
	 * Alternates between getting moves from BlockingQueue and sending them via sockets.
	 * 
	 * @param serverIP the ip the socket server is hosted on
	 * @param listener the listener to send updates to
	 * @param game the game object reference
	 */
	public void joinServer(String serverIP, GameReadyListener listener, Game game) {
		System.out.println("Connecting to the server...");
		
		try (
            Socket socket = new Socket(serverIP, port);
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream())
        ) {
            String c = input.readUTF();
            int row = input.read();
            int col = input.read();
            int seed = Integer.parseInt(input.readUTF());
            game.initializeGame(row, col, seed);
            
            switch(c.toLowerCase()) {
	        	case "white": 
	        		color = Color.BLACK; 
	        		break;
	        	case "black": 
	        		color = Color.WHITE; 
	        		break;
	        }
            
	        listener.start(color);
	        listener.update();
            
            System.out.println("Connected to the server.[" + color.toString() + "]");

            while (game.getGameState() == null) {
            	String move;
            	if (game.currentTurn == color) {
            		move = moves.take();
                    output.writeUTF(move);
                    output.flush();
            	} else {
            		move = input.readUTF();
            		System.out.println("Message from server: " + move);
            	}
            	
            	try {
            		game.turn(move);
            		game.printBoard();
        	        listener.update();
            	} catch (Exception e) {
					System.out.println(e.getMessage());
				}
            }
        } catch (EOFException e) {
            System.out.println("Connection closed by the server.");
        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        } catch (InterruptedException e) {
        	System.out.println("Client error(Interrupted): " + e.getMessage());
		}
	}
}
