package CST8221;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class View {

    private Model model;
    private Controller controller;
    private boolean placingShipsMode = false;

    public View(Model model, Controller controller) {
        this.model = model;
        this.controller = controller;
        initializeComponents();
        initializeListeners();
    }

    private JFrame mainWindow = new JFrame("Battleship");
    private JPanel serverPanel = new JPanel();
    private JFrame serverWindow = new JFrame ("Server");
    private JLabel ipLabel = new JLabel("IP Address");
    private JTextField ipTextField = new JTextField(30);
    private JButton connectButton = new JButton("Connect");
    
    private void createServerWindow() {
    	connectButton.addActionListener(e -> {
    		controller.connect(ipTextField.getText());
    		serverWindow.setVisible(false);
    	});
    	serverPanel.add(ipLabel);
    	serverPanel.add(ipTextField);
    	serverPanel.add(connectButton);
    	//serverPanel.setLayout(mainLayout);
    	serverWindow.add(serverPanel);
        serverWindow.setSize(655, 100);
        //serverWindow.setVisible(true);
    }
    private JFrame connectionWindow = new JFrame ("Connection");  
    private JPanel mainPanel = new JPanel();
    private JPanel westPanel = new JPanel();
    private JPanel centerPanel = new JPanel();
    private JPanel eastPanel = new JPanel();
    private JPanel scorePanel = new JPanel();
    private JPanel chatPanel = new JPanel();
    private JButton lifelineButton = new JButton("Lifeline");
    private JButton redoButton = new JButton("Redo");
    private JButton swapButton = new JButton("Swap");
    private JButton sendButton = new JButton("Send");
    private JButton toggleDirectionButton = new JButton("Toggle Direction");

    private BorderLayout mainLayout = new BorderLayout();
    private GridLayout westLayout = new GridLayout(5, 1);
    private BorderLayout eastLayout = new BorderLayout();
    private GridLayout centerLayout = new GridLayout(10, 10); // Ensure grid layout is 10x10
    private GridLayout scoreLayout = new GridLayout(2, 2);
    private BorderLayout chatLayout = new BorderLayout();
    private JLabel labelOne = new JLabel("", SwingConstants.CENTER);
    private JTextArea inputText = new JTextArea(3, 100);
    private JLabel eastLabel = new JLabel("", SwingConstants.CENTER);
    private JLabel scoreName1 = new JLabel("Player 1");
    private JLabel scoreName2 = new JLabel("Player 2");
    private JLabel scoreValue1 = new JLabel("0");
    private JLabel scoreValue2 = new JLabel("0");

    private JTextArea outputText = new JTextArea(20, 25);
    private JScrollPane scrollPane = new JScrollPane(outputText);
    private JMenuBar menuBar = new JMenuBar();
    private JMenu fileMenu = new JMenu("File");
    private JMenu gameMenu = new JMenu("Game");
    private JMenu languageMenu = new JMenu("Language");
    private JMenu networkMenu = new JMenu("Network");
    private JMenu helpMenu = new JMenu("Help");
    private JMenuItem saveMenuItem = new JMenuItem("Save");
    private JMenuItem newMenuItem = new JMenuItem("New");
    private JMenuItem EnglishLan = new JMenuItem("English");
    private JMenuItem FrenchLan = new JMenuItem("French");
    private JMenuItem helpMenuItem = new JMenuItem("Help");
    
    private JLabel labelArray[][];

    // Submenu items
    private JMenuItem loadMenuItem = new JMenuItem("Load");
    private JMenuItem exitMenuItem = new JMenuItem("Exit");
    private JMenuItem resumeMenuItem = new JMenuItem("Resume");
    private JMenuItem pauseMenuItem = new JMenuItem("Pause");
    private JMenuItem startServerItem = new JMenuItem("Start Server");
    private JMenuItem connectItem = new JMenuItem("Connect");
    private JMenuItem stopServerItem = new JMenuItem("Stop Server");
    private JMenuItem disconnectItem = new JMenuItem("Disconnect");
    

    private Map<String, String> englishTranslations;
    private Map<String, String> frenchTranslations;

    private ImageIcon shipH1, shipH2, shipH5;
    private ImageIcon shipV1, shipV2, shipV5;
    private ImageIcon blast, water;

    private void initializeComponents() {
        ImageIcon labelOneIcon = new ImageIcon(("Assets/logo.png"));
        labelOne.setIcon(labelOneIcon);

        shipH1 = new ImageIcon(("Assets/bow_west.png"));
        shipH2 = new ImageIcon(("Assets/midhull_horiz.png"));
        shipH5 = new ImageIcon(("Assets/bow_east.png"));

        shipV1 = new ImageIcon(("Assets/bow_north.png"));
        shipV2 = new ImageIcon(("Assets/midhull_vert.png"));
        shipV5 = new ImageIcon(("Assets/bow_south.png"));

        blast = new ImageIcon(("Assets/hit.png"));
        water = new ImageIcon(("Assets/miss.png"));

        // Create ship icons
        labelArray = new JLabel[10][10]; // Ensure grid size is 10x10
        centerPanel.setLayout(centerLayout);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                labelArray[i][j] = new JLabel();
                labelArray[i][j].setBorder(new LineBorder(Color.BLACK));
                labelArray[i][j].setOpaque(true);
                labelArray[i][j].putClientProperty('x', j);
                labelArray[i][j].putClientProperty('y', i);
                labelArray[i][j].addMouseListener(controller);
                centerPanel.add(labelArray[i][j]);
            }
        }
    }
    
    private void sendMessage() {
    	String message = inputText.getText();
    	controller.send(message);
    	inputText.setText("");
    }
    
    public void displayText(String text) {
    	outputText.append(text +"\n");
    }
    
    private void initializeListeners() {
        lifelineButton.addActionListener(e -> controller.revealOpponentShip());
        redoButton.addActionListener(e -> controller.redoLastMove());
        swapButton.addActionListener(e -> controller.swapBoards());
        toggleDirectionButton.addActionListener(e -> controller.toggleDirection());

        newMenuItem.addActionListener(e -> controller.newGame());
        saveMenuItem.addActionListener(e -> controller.saveGame());
        loadMenuItem.addActionListener(e -> controller.loadGame());
        exitMenuItem.addActionListener(e -> System.exit(0));
        helpMenuItem.addActionListener(e -> showHelpDialog());
        startServerItem.addActionListener(e -> controller.startServer());
        stopServerItem.addActionListener(e -> controller.stopServer());
        connectItem.addActionListener(e -> serverWindow.setVisible(true));
        disconnectItem.addActionListener(e -> controller.disconnect());
        sendButton.addActionListener(e -> sendMessage());

        EnglishLan.addActionListener(e -> switchLanguage("EN"));
        FrenchLan.addActionListener(e -> switchLanguage("FR"));
    }

    public void printMsg(String msg) {
        outputText.append(msg + "\n");
    }

    public void clearMessages() {
        outputText.setText("");
    }

    public void redrawBoard() {
        final int size = model.getSize();
        char[][] board = model.getBoard();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                JLabel b = labelArray[row][col];
                char c = board[col][row];
                switch (c) {
                case '0':
                    b.setBackground(Color.white);
                    b.setIcon(null);
                    break;
                case '1':
                    // Display Player 1's ship icons only if in player view
                    if (model.isPlayerView()) {
                        b.setIcon(shipV2); // Middle part of a vertical ship
                    } else {
                        b.setBackground(Color.white);
                        b.setIcon(null);
                    }
                    break;
                case 'a':
                    // Display Player 1's ship icons only if in player view
                    if (model.isPlayerView()) {
                        b.setIcon(shipV5); // Middle part of a vertical ship
                        if (model.getDirection(col, row) == 0) {
                            b.setIcon(shipH5);
                        }
                    } else {
                        b.setBackground(Color.white);
                        b.setIcon(null);
                    }
                    break;
                case 'A':
                    // Display Player 1's ship icons only if in player view
                    if (model.isPlayerView()) {
                        b.setIcon(shipV1); // Middle part of a vertical ship
                        if (model.getDirection(col, row) == 0) {
                            b.setIcon(shipH1);
                        }
                    } else {
                        b.setBackground(Color.white);
                        b.setIcon(null);
                    }
                    break;
                case '2':
                case 'b':
                case 'B':
                    // Hide Player 2's ship icons if in player view
                    if (model.isPlayerView()) {
                        b.setBackground(Color.white);
                        b.setIcon(null);
                    } else {
                        // Show hits and misses on opponent's board
                        if (c == '2') {
                            b.setBackground(Color.white);
                            b.setIcon(null);
                        }
                    }
                    break;
                    
                case 'c':
                	if (model.isPlayerView())
                    b.setIcon(water); // Miss
                    break;
                    
                case 'd':
                	if (model.isPlayerView())
                    b.setIcon(blast); // Hit
                    break;
                case 'C':
                	if (!model.isPlayerView())
                    b.setIcon(water); // Miss
                	else 
                		b.setIcon(null);
                    break;
                    
                case 'D':
                	if (!model.isPlayerView())
                    b.setIcon(blast); // Hit
                	else 
                		b.setIcon(null);
                    break;
                default:
                    b.setIcon(null);
                    b.setBackground(Color.white);
                    break;
                }
            }
        }
        updateScores();
    }

    public void updateScores() {
        scoreValue1.setText(String.valueOf(model.getPlayer1Score()));
        scoreValue2.setText(String.valueOf(model.getPlayer2Score()));
    }

    public void enterShipPlacementMode() {
        placingShipsMode = true;
        printMsg("Entering ship placement mode. Click to place ships.");
    }

    public void draw() {
        try {
            englishTranslations = LanguageLoader.loadLanguageFile("D:\\JAP\\Battleship\\src\\CST8221\\english.txt");
            frenchTranslations = LanguageLoader.loadLanguageFile("D:\\JAP\\Battleship\\src\\CST8221\\french.txt");
        } catch (IOException e) {
            e.printStackTrace();
            englishTranslations = getDefaultEnglishTranslations();
            frenchTranslations = getDefaultFrenchTranslations();
        }

        gameMenu.add(resumeMenuItem);
        gameMenu.add(pauseMenuItem);

        resumeMenuItem.addActionListener(e -> System.out.println("Resume action triggered"));
        pauseMenuItem.addActionListener(e -> System.out.println("Pause action triggered"));

        fileMenu.add(newMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(loadMenuItem);
        fileMenu.add(exitMenuItem);

        languageMenu.add(EnglishLan);
        languageMenu.add(FrenchLan);
        networkMenu.add(startServerItem);
        networkMenu.add(stopServerItem);
        networkMenu.add(connectItem);
        networkMenu.add(disconnectItem);
        

        helpMenu.add(helpMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(gameMenu);
        menuBar.add(languageMenu);
        menuBar.add(networkMenu);
        menuBar.add(helpMenu);
        mainWindow.setJMenuBar(menuBar);

        fileMenu.setText("File");
        gameMenu.setText("Game");
        languageMenu.setText("Language");
        networkMenu.setText("Network");
        helpMenu.setText("Help");
        saveMenuItem.setText("Save");
        newMenuItem.setText("New");
        loadMenuItem.setText("Load");
        exitMenuItem.setText("Exit");
        resumeMenuItem.setText("Resume");
        pauseMenuItem.setText("Pause");
        helpMenuItem.setText("Help");

        fileMenu.setMnemonic('F');
        gameMenu.setMnemonic('G');
        languageMenu.setMnemonic('L');
        networkMenu.setMnemonic('N');
        helpMenu.setMnemonic('H');

        labelOne.setFont(new Font("Times New Roman", Font.PLAIN, 50));
        eastLabel.setFont(new Font("Times New Roman", Font.PLAIN, 30));

        westLayout.setColumns(1);
        westPanel.setLayout(westLayout);
        westPanel.add(lifelineButton);
        westPanel.add(redoButton);
        westPanel.add(swapButton);
        westPanel.add(toggleDirectionButton);

        eastPanel.setLayout(eastLayout);
        eastPanel.add(scrollPane, BorderLayout.CENTER);
        eastPanel.add(eastLabel, BorderLayout.NORTH);
        scorePanel.setLayout(scoreLayout);
        scorePanel.add(scoreName1);
        scorePanel.add(scoreValue1);
        scorePanel.add(scoreName2);
        scorePanel.add(scoreValue2);
        eastPanel.add(scorePanel, BorderLayout.SOUTH);
        chatPanel.setLayout(chatLayout);
        chatPanel.add(inputText, BorderLayout.CENTER);
        chatPanel.add(sendButton, BorderLayout.EAST);

        mainPanel.setLayout(mainLayout);
        mainPanel.add(labelOne, BorderLayout.NORTH);
        mainPanel.add(chatPanel, BorderLayout.SOUTH);
        mainPanel.add(eastPanel, BorderLayout.EAST);
        mainPanel.add(westPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainWindow.add(mainPanel);
        mainWindow.setSize(755, 500);
        mainWindow.setVisible(true);
        createServerWindow();
        enableLifeline(false);
        enableRedo(false); // Initialize redo button to be disabled

        switchLanguage("EN");
    }

    public void showGameOver(String message) {
        JOptionPane.showMessageDialog(mainWindow, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showHelpDialog() {
        String helpMessage = "Battleship Game Instructions:\n\n" + "1. Each player places their ships on the grid.\n"
                + "2. Players take turns to attack by clicking on the grid.\n"
                + "3. The game ends when all of one player's ships are sunk.\n"
                + "4. Use the buttons and menus for additional actions like saving or loading a game.\n"
                + "5. Use the lifeline button to reveal one ship of the opponent.";
        JOptionPane.showMessageDialog(mainWindow, helpMessage, "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    public void resetView(int size) {
        centerPanel.removeAll();
        labelArray = new JLabel[size][size];
        centerLayout = new GridLayout(size, size);
        centerPanel.setLayout(centerLayout);

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                JLabel b = new JLabel("");
                b.putClientProperty('x', col);
                b.putClientProperty('y', row);
                b.addMouseListener(controller);
                b.setBorder(new LineBorder(Color.BLACK));
                labelArray[row][col] = b;
                b.setOpaque(true);
                centerPanel.add(b);
            }
        }

        centerPanel.revalidate();
        centerPanel.repaint();
        clearMessages();
        printMsg("New game started. Place your ships.");
    }

    public void enableLifeline(boolean enable) {
        lifelineButton.setEnabled(enable);
    }

    public void enableRedo(boolean enable) {
        redoButton.setEnabled(enable);
    }

    public void highlightCell(int row, int col, boolean highlight) {
        labelArray[row][col].setBackground(highlight ? Color.GREEN : Color.WHITE);
    }

    private void switchLanguage(String lang) {
        Map<String, String> translations = lang.equals("EN") ? englishTranslations : frenchTranslations;

        lifelineButton.setText(translations.get("lifelineButton"));
        redoButton.setText(translations.get("redoButton"));
        swapButton.setText(translations.get("swapButton"));
        sendButton.setText(translations.get("sendButton"));
        toggleDirectionButton.setText(translations.get("toggleDirectionButton"));
        fileMenu.setText(translations.get("fileMenu"));
        gameMenu.setText(translations.get("gameMenu"));
        languageMenu.setText(translations.get("languageMenu"));
        networkMenu.setText(translations.get("networkMenu"));
        helpMenu.setText(translations.get("helpMenu"));
        saveMenuItem.setText(translations.get("saveMenuItem"));
        newMenuItem.setText(translations.get("newMenuItem"));
        loadMenuItem.setText(translations.get("loadMenuItem"));
        exitMenuItem.setText(translations.get("exitMenuItem"));
        resumeMenuItem.setText(translations.get("resumeMenuItem"));
        pauseMenuItem.setText(translations.get("pauseMenuItem"));
        helpMenuItem.setText(translations.get("helpMenuItem"));
        eastLabel.setText(translations.get("eastLabel"));
        scoreName1.setText(translations.get("scoreName1"));
        scoreName2.setText(translations.get("scoreName2"));
    }

    private Map<String, String> getDefaultEnglishTranslations() {
        Map<String, String> translations = new HashMap<>();
        translations.put("lifelineButton", "Lifeline");
        translations.put("redoButton", "Redo");
        translations.put("swapButton", "Swap");
        translations.put("sendButton", "Send");
        translations.put("toggleDirectionButton", "Toggle Direction");
        translations.put("fileMenu", "File");
        translations.put("gameMenu", "Game");
        translations.put("languageMenu", "Language");
        translations.put("networkMenu", "Network");
        translations.put("helpMenu", "Help");
        translations.put("saveMenuItem", "Save");
        translations.put("newMenuItem", "New");
        translations.put("loadMenuItem", "Load");
        translations.put("exitMenuItem", "Exit");
        translations.put("resumeMenuItem", "Resume");
        translations.put("pauseMenuItem", "Pause");
        translations.put("helpMenuItem", "Help");
        translations.put("eastLabel", "User 1's Turn");
        translations.put("scoreName1", "User 1");
        translations.put("scoreName2", "User 2");
        return translations;
    }

    private Map<String, String> getDefaultFrenchTranslations() {
        Map<String, String> translations = new HashMap<>();
        translations.put("lifelineButton", "Ligne de vie");
        translations.put("redoButton", "Refaire");
        translations.put("swapButton", "Échanger");
        translations.put("sendButton", "Envoyer");
        translations.put("toggleDirectionButton", "Changer de direction");
        translations.put("fileMenu", "Fichier");
        translations.put("gameMenu", "Jeu");
        translations.put("languageMenu", "Langue");
        translations.put("networkMenu", "Réseau");
        translations.put("helpMenu", "Aide");
        translations.put("saveMenuItem", "Sauvegarder");
        translations.put("newMenuItem", "Nouveau");
        translations.put("loadMenuItem", "Charger");
        translations.put("exitMenuItem", "Quitter");
        translations.put("resumeMenuItem", "Reprendre");
        translations.put("pauseMenuItem", "Pause");
        translations.put("helpMenuItem", "Aide");
        translations.put("eastLabel", "Tour de l'utilisateur 1");
        translations.put("scoreName1", "Utilisateur 1");
        translations.put("scoreName2", "Utilisateur 2");
        return translations;
    }

    public void placeShipIcons(int x, int y, int length, boolean horizontal) {
        if (length == 2) {
            if (horizontal) {
                labelArray[y][x].setIcon(shipH1);
                labelArray[y][x + 1].setIcon(shipH5);
            } else {
                labelArray[y][x].setIcon(shipV1);
                labelArray[y + 1][x].setIcon(shipV5);
            }
        } else {
            if (horizontal) {
                labelArray[y][x].setIcon(shipH1);
                for (int i = 1; i < length - 1; i++) {
                    labelArray[y][x + i].setIcon(shipH2);
                }
                labelArray[y][x + length - 1].setIcon(shipH5);
            } else {
                labelArray[y][x].setIcon(shipV1);
                for (int i = 1; i < length - 1; i++) {
                    labelArray[y + i][x].setIcon(shipV2);
                }
                labelArray[y + length - 1][x].setIcon(shipV5);
            }
        }
    }

    public void setModel(Model model2) {
        this.model = model2;
    }
}
