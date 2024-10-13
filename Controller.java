package CST8221;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import java.io.*;
import java.net.InetAddress;
import java.util.Random;

public class Controller implements MouseListener {
	private String type; 
    private Model model;
    private View view;
    private boolean placingShips = true;
    private int currentShipIndex = 0; // Index to track the current ship
    private boolean horizontal = true; // Ship placement direction
    private Random rand = new Random(); // Random object for placing computer ships and making computer moves
    
    public void startServer() {
    	type = "server";
    	System.out.println("Starting Server");
    	int portNumber = 3000;
    	ts = new ThreadServer(this,portNumber);
		try {
			
			Thread serverThread = new Thread(ts);
			serverThread.start();
			System.out.println("Server running on " + InetAddress.getLocalHost() + " at port " + portNumber + "!");
		} catch (Exception e) {
			System.out.println("Error: " + e.toString());
		}
    }
    
    public void display(String text) {
    	view.displayText(text);
    	
    }
    
    public void stopServer() {
    	type = null;
    	System.out.println("Stopping Server");
    	if (ts != null) {
    		try {
				ts.stopServer();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    }
    private ThreadClient c;
    
    public void connect(String hostName) {
    	type = "client";
    	System.out.println("Connecting");
    	int portNumber = 3000;
    	c = new ThreadClient(this,hostName,portNumber);
    	
    	Thread t = new Thread(c);
    	t.start();
		
    }
    
    
    public void disconnect() {
    	type = null;
    	System.out.println("Disconnecting");
    }
    
    public void send(String message) {
    	if (type=="client") {
    		c.sendMessage(message);
    	}
    	else if (type == "server") {
    		ts.sendMessage(message);
    	}
    	
    }

    private ThreadServer ts;
    
    public Controller(int size) {
    	
    	
        model = new Model(size);
        view = new View(model, this);
    }

    public void start() {
        view.draw();
        placePlayerShips();
        placeComputerShips();
        view.enableLifeline(false);
        view.enableRedo(false);
    }

    public static void main(String[] args) {
    	SplashScreen splash = new SplashScreen(5);
    	splash.showSplashWindow();
    	
        Controller controller = new Controller(10); // Set grid size here
        controller.start();
    }

    public void placePlayerShips() {
        view.printMsg("Please place your ships on the board.");
        view.enterShipPlacementMode();
    }

    private int[] getShipLengths() {
        return new int[]{2, 3, 3, 4, 5}; // Default ship lengths
    }

    public void placeComputerShips() {
        int[] shipLengths = getShipLengths();
        for (int length : shipLengths) {
            boolean placed = false;
            int attempts = 0;
            while (!placed && attempts < 100) { // Try up to 100 times to place each ship
                placed = model.tryPlaceShip(2, length, rand.nextInt(model.getSize()), rand.nextInt(model.getSize()), rand.nextBoolean());
                attempts++;
            }
            if (!placed) {
                System.out.println("Failed to place computer ship of length " + length + " after " + attempts + " attempts.");
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (placingShips) {
            handleShipPlacement(e);
        } else {
            handlePlayerMove(e);
        }
    }

    public void mouseEntered1(MouseEvent e) {
        if (placingShips) {
            highlightShipPlacement(e);
        }
    }

    public void mouseExited1(MouseEvent e) {
        if (placingShips) {
            clearShipPlacementHighlight(e);
        }
    }

    private void handleShipPlacement(MouseEvent e) {
        Object o = e.getSource();
        if (o instanceof JLabel l) {
            int x = (int) l.getClientProperty('x');
            int y = (int) l.getClientProperty('y');

            int[] shipLengths = getShipLengths();
            int shipLength = shipLengths[currentShipIndex];
            boolean placed = model.tryPlaceShip(1, shipLength, x, y, horizontal);
            if (placed) {
                view.printMsg("Player 1 placed a ship of length " + shipLength + " at " + x + "," + y);
                view.placeShipIcons(x, y, shipLength, horizontal);
                currentShipIndex++;
                if (currentShipIndex >= shipLengths.length) {
                    placingShips = false;
                    view.printMsg("All ships placed. Start the game!");
                    model.setCurrentPlayer(1); // Start with Player 1
                }
            } else {
                view.printMsg("Failed to place ship. Try again.");
            }
        }
    }

    private void handlePlayerMove(MouseEvent e) {
        Object o = e.getSource();
        if (o instanceof JLabel l) {
            int x = (int) l.getClientProperty('x');
            int y = (int) l.getClientProperty('y');
            int move = model.makeMove(x, y);
            if (move != 0) {
                view.printMsg("Player " + model.getPlayer() + " clicked on " + x + "," + y);
                if (move == 2) {
                    view.printMsg("Player " + model.getPlayer() + " hit a ship at " + x + "," + y);
                    if (!model.isLifelineUsed()) {
                        model.activateLifeline();
                        view.enableLifeline(true);
                    }
                } else {
                    view.printMsg("Player " + model.getPlayer() + " missed at " + x + "," + y);
                }
                if (model.isGameOver()) {
                    view.printMsg("Game over! Player " + (model.getPlayer() == 1 ? 2 : 1) + " wins!");
                    view.showGameOver("Game over! Player " + (model.getPlayer() == 1 ? 2 : 1) + " wins!");
                } else {
                    model.nextPlayer();
                    view.redrawBoard();
                    if (model.getPlayer() == 2) {
                        computerMove();
                    }
                    view.enableRedo(true); // Enable redo button for the new player
                }
                view.updateScores(); // Update the scores after each move
            }
        }
    }

    private void highlightShipPlacement(MouseEvent e) {
        Object o = e.getSource();
        if (o instanceof JLabel l) {
            int x = (int) l.getClientProperty('x');
            int y = (int) l.getClientProperty('y');

            int[] shipLengths = getShipLengths();
            int shipLength = shipLengths[currentShipIndex];

            for (int i = 0; i < shipLength; i++) {
                if (horizontal) {
                    if (x + i < model.getSize()) {
                        view.highlightCell(y, x + i, true);
                    }
                } else {
                    if (y + i < model.getSize()) {
                        view.highlightCell(y + i, x, true);
                    }
                }
            }
        }
    }

    private void clearShipPlacementHighlight(MouseEvent e) {
        Object o = e.getSource();
        if (o instanceof JLabel l) {
            int x = (int) l.getClientProperty('x');
            int y = (int) l.getClientProperty('y');

            int[] shipLengths = getShipLengths();
            int shipLength = shipLengths[currentShipIndex];

            for (int i = 0; i < shipLength; i++) {
                if (horizontal) {
                    if (x + i < model.getSize()) {
                        view.highlightCell(y, x + i, false);
                    }
                } else {
                    if (y + i < model.getSize()) {
                        view.highlightCell(y + i, x, false);
                    }
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public void computerMove() {
        int x, y, move;
        do {
            x = rand.nextInt(model.getSize());
            y = rand.nextInt(model.getSize());
            move = model.makeMove(x, y);
        } while (move == 0);
        if (move != 0) {
            view.printMsg("Computer clicked on " + x + "," + y);
            if (move == 2) {
                view.printMsg("Computer hit a ship at " + x + "," + y);
            } else {
                view.printMsg("Computer missed at " + x + "," + y);
            }
            if (model.isGameOver()) {
                view.printMsg("Game over! Computer wins!");
                view.showGameOver("Game over! Computer wins!");
            } else {
                if (move == 2 && !model.isLifelineActivated() && !model.isLifelineUsed()) {
                    model.useLifeline();
                    view.enableLifeline(true);
                }
                model.nextPlayer();
                view.redrawBoard();
            }
            view.updateScores(); // Update the scores after each move
        }
    }

    // Toggle the direction of ship placement
    public void toggleDirection() {
        horizontal = !horizontal;
        view.printMsg("Ship placement direction changed to " + (horizontal ? "horizontal" : "vertical"));
    }

    // Start a new game
    public void newGame() {
        model = new Model(model.getSize());
        view.resetView(model.getSize());
        placingShips = true;
        currentShipIndex = 0;
        horizontal = true;
        view.clearMessages();
        view.printMsg("New game started. Place your ships.");
        placePlayerShips();
        placeComputerShips();
        view.enableLifeline(false);
        view.enableRedo(false);
    }

    // Reveal one of the opponent's ships
    public void revealOpponentShip() {
        if (model.isLifelineUsed()) {
            view.printMsg("Lifeline has already been used.");
            return;
        }

        boolean revealed = false;
        while (!revealed) {
            int x = rand.nextInt(model.getSize());
            int y = rand.nextInt(model.getSize());
            if (model.getPlayer() == 1 && (model.get(x, y) == '2' || model.get(x, y) == 'b' || model.get(x, y) == 'B')) {
                model.set(x, y, '4'); // Mark as revealed
                view.redrawBoard();
                view.printMsg("Lifeline used! Revealed a part of the opponent's ship at (" + x + ", " + y + ")");
                revealed = true;
            } else if (model.getPlayer() == 2 && (model.get(x, y) == '1' || model.get(x, y) == 'a' || model.get(x, y) == 'A')) {
                model.set(x, y, '4'); // Mark as revealed
                view.redrawBoard();
                view.printMsg("Lifeline used! Revealed a part of the opponent's ship at (" + x + ", " + y + ")");
                revealed = true;
            }
        }
        model.deactivateLifeline();
        view.enableLifeline(false);
    }

    // Save the game state to a file
    public void saveGame() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("game_save.dat"))) {
            out.writeObject(model);
            view.printMsg("Game saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            view.printMsg("Failed to save the game.");
        }
    }

    // Load the game state from a file
    public void loadGame() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("game_save.dat"))) {
            model = (Model) in.readObject();
            view.setModel(model); // Update the view with the loaded model
            view.redrawBoard();
            view.updateScores(); // Update the scores after loading the game
            view.printMsg("Game loaded successfully.");
        } catch (FileNotFoundException e) {
            view.printMsg("No saved game found.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            view.printMsg("Failed to load the game.");
        }
    }

    // Swap the boards
    public void swapBoards() {
        model.swapBoards();
        view.redrawBoard();
        view.printMsg("Boards have been swapped.");
    }

    // Redo the last move for the current player
    public void redoLastMove() {
        if (model.getPlayer() == 1 && !model.isPlayer1RedoUsed()) {
            model.redoLastMove();
            model.setPlayer1RedoUsed(true);
            view.redrawBoard();
            view.printMsg("Player 1's last move has been redone.");
        } else if (model.getPlayer() == 2 && !model.isPlayer2RedoUsed()) {
            model.redoLastMove();
            model.setPlayer2RedoUsed(true);
            view.redrawBoard();
            view.printMsg("Player 2's last move has been redone.");
        } else {
            view.printMsg("Redo has already been used.");
        }
    }
}
