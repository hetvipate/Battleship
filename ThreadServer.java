package CST8221;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

/**
 * Class ThreadServer - A simple multi-threaded server implementation.
 * Author: Hetvi Patel
 */
public class ThreadServer implements Runnable {
	private Controller controller;
	
	/**
	 * Client socket.
	 */
	
	
	/**
	 * Counter for connected clients.
	 */
	private int totalClients = 0, currentClients = 0;
	
	/**
	 * Server socket.
	 */
	private ServerSocket serverSocket;
	
	/**
	 * Default port number.
	 */
	private static final int DEFAULT_PORT = 3000;
	
	/**
	 * Port number to bind the server to.
	 */
	private int portNumber = DEFAULT_PORT;

	/**
	 * Default constructor.
	 */
	public ThreadServer(Controller controller,int portNumber) {
		this.portNumber = portNumber;
		this.controller = controller;
		
	}
	
	public void startServer(){
		
		
	}
	
	/**
	 * Main method to start the server.
	 * @param args Command-line arguments for the port number.
	 */
	public static void main(String[] args) {
		int portNumber;
    	// Process command-line arguments
    	if (args == null || args.length < 1) {
            portNumber = DEFAULT_PORT;
            System.out.println("Using default port: " + DEFAULT_PORT);
        } else {
            portNumber = Integer.parseInt(args[0]);
        }

        System.out.println("Starting Server Thread on port " + portNumber);
        
        
        ThreadServer ts = new ThreadServer(null,portNumber);
		try {
			
			Thread serverThread = new Thread(ts);
			serverThread.start();
			System.out.println("Server running on " + InetAddress.getLocalHost() + " at port " + portNumber + "!");
		} catch (Exception e) {
			System.out.println("Error: " + e.toString());
		}
	}

	/**
	 * Run method for accepting client connections.
	 * @throws IOException 
	 */
	public void stopServer() throws IOException {
		running = false;
		
		serverSocket.close();
		
	}
	private boolean running = true;
	LinkedList<Connection> connections = new LinkedList<>();
	
	public void run() {
		try {
			serverSocket = new ServerSocket(portNumber);
			while (running) {
				try {
					
					// Create and start a new thread for the connected client
					Connection clientHandler = new Connection();
					connections.add(clientHandler);
					
					
					clientHandler.start();
				} catch (IOException ioe) {
					System.out.println("Hello");
					System.out.println(ioe);
					for (Connection c:connections) {
						c.disconnect();
						
					}
					break;
				}
			}
			
			
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		
	}
	
	public void sendMessage(String text) {
		
		for (Connection c:connections) {
			c.sendMessage(text);
			
		}
		
	}
	
	

	/**
	 * Inner class for handling client connections.
	 */
	class Connection extends Thread {
		
		/**
		 * Client socket.
		 */
		private Socket clientSocket;
		
		/**
		 * Client ID.
		 */
		private int clientId;
		
		/**
		 * Constructor for initializing client handler.
		 * @param socket Client socket
		 * @param clientId Client ID
		 * @throws IOException 
		 */
		public Connection() throws IOException {
			clientSocket = serverSocket.accept();
			totalClients++;
			currentClients++;
			System.out.println("Connecting " + clientSocket.getInetAddress() + " at port " + clientSocket.getPort() + ".");
			
			this.clientId = totalClients;
		}
		public void disconnect() throws IOException {
			System.out.println("Disconnecting " + clientSocket.getInetAddress() + "!");
			clientSocket.close();
			currentClients--;
			System.out.println("Current client number: " + totalClients);
			
			if (currentClients == 0) {
				System.out.println("No more clients connected. Server is shutting down.");
				
				stopServer();
				
			}
			
		}

		/**
		 * Run method for handling client communication.
		 */
		
		private PrintStream out;
		BufferedReader in;
		public void run() {
			try 
				  {out = new PrintStream(clientSocket.getOutputStream());
					in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				
				out.println(clientId);  // Send client ID to the client
				
				String data = in.readLine();
				while (data != null && !data.endsWith("#end")) {
					int separatorIndex = data.indexOf("#");
					String clientStrId = data.substring(0, separatorIndex);
					String clientData = data.substring(separatorIndex + 1);
					
					System.out.println("Client[" + clientStrId + "]: " + clientData);
					controller.display(clientData);
					out.println("String \"" + clientData + "\" received.");
					out.flush();
					
					data = in.readLine();
				}
			in.close();
			out.close();
				
				
			} catch (IOException ioe) {
				System.out.println(ioe);
			}
		}
		public void sendMessage(String text) {
			out.println(text);
			out.flush();
			
		}
	}
}
