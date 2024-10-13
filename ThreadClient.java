package CST8221;

import java.net.*;
import java.io.*;

/**
 * Class ThreadClient - A simple client for connecting to a server.
 * Author: Hetvi Patel
 */
public class ThreadClient implements Runnable{
	private Controller controller;
	
	/**
	 * Default port number.
	 */
	private static final int DEFAULT_PORT = 3000;
	
	/**
	 * Default hostname.
	 */
	private static final String DEFAULT_HOSTNAME = "localhost";
	
	/**
	 * Port number to connect to.
	 */
	private int portNumber = DEFAULT_PORT;
	
	/**
	 * Hostname to connect to.
	 */
	private String hostName = DEFAULT_HOSTNAME;

	/**
	 * Default constructor.
	 */
	public ThreadClient(Controller controller,String hostName,int portNumber) {
		this.hostName = hostName;
		this.portNumber = portNumber;
		this.controller = controller;
	}
	
	private BufferedReader serverInput;
	private PrintStream serverOutput;
	private BufferedReader userInput; 
	private String clientID;
	private Socket socket;
	
	public void stopClient() {
		try {
			userInput.close();
			serverOutput.close();
			serverInput.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	class Receive implements Runnable {
		

		@Override
		public void run() {
			String serverMessage;	
			while (running) {
				try {
					serverMessage = serverInput.readLine();
					if (serverMessage == null) {
						running = false;
						break;
					}
					System.out.println("\nServer: " + serverMessage);
					controller.display(serverMessage);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
			
		}
		
	}
	
	private boolean running;
	
	private void send() throws IOException {
		running = true;
		String userMessage;
		
		Runnable r = new Receive();
		Thread t = new Thread(r);
		t.start();
		
		
		// Initial prompt to the user
		
		
		do {
			System.out.print("Client[" + clientID + "]: ");
			userMessage = userInput.readLine();
			String messageToSend = clientID + "#" + userMessage;
			serverOutput.println(messageToSend);
			serverOutput.flush();
			
		}
		while(!userMessage.equalsIgnoreCase("end")&& running);
		
		
		running = false;
		
		
	}
	
	public void startClient(){
		try {
			socket = new Socket(hostName, portNumber);
				 serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				 serverOutput = new PrintStream(socket.getOutputStream());
				 userInput = new BufferedReader(new InputStreamReader(System.in)); 
				
				clientID = serverInput.readLine();
				System.out.println("Connected as client no. " + clientID);
				
				
			send();	
			} catch (IOException e) {
				System.out.println("Error: " + e.getMessage());
			}
		
		
	}
	
	static class Display extends Thread{
		public void run() {
			for (int i = 0; i < 100; i++) {
				System.out.println("Hello");
				try {
					Thread.sleep(7000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	/**
	 * Main method to run the client.
	 * @param args Command-line arguments for hostname and port number.
	 */
	public static void main(String[] args) {
		String hostName = DEFAULT_HOSTNAME;
		int portNumber = DEFAULT_PORT;
		// Process command-line arguments
		if (args == null || args.length != 2) {
			System.out.println("Usage: java ThreadClient <hostname> <port number>");
			System.out.println("Using default values: hostname = " + DEFAULT_HOSTNAME + ", port = " + DEFAULT_PORT);
		} else {
			hostName = args[0];
			portNumber = Integer.parseInt(args[1]);
		}
		
		System.out.println("Connecting to server at " + hostName + " on port " + portNumber);
		
		//Thread t = new Display();
		//t.start();
		ThreadClient c = new ThreadClient(null,hostName,portNumber);
		Thread t = new Thread(c);
		t.start();
		
		
	}
	
		public void sendMessage(String message) {
			String messageToSend = clientID + "#" + message;
			serverOutput.println(messageToSend);
			serverOutput.flush();
    	
    }
    

	@Override
	public void run() {
		startClient();
		stopClient();
		
	}
}
