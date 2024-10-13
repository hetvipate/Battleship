**Battleship Game - UI and Backend Design**

This repository contains the implementation of the Battleship game, developed as part of a Computer Science Challenge. The project focuses on both UI and backend development, offering an engaging game experience through Java Swing for the UI and a robust backend for game logic and multiplayer support.

Features
User Interface (UI)
Graphical Interface: A dynamic and user-friendly UI designed using Java Swing.
Customizable Layout: The game allows players to select from different grid sizes: 5x5, 10x10, or 12x12.
Multilingual Support: The interface supports English and French, with easy switching between languages.
Interactive Components: Includes buttons for key actions like lifelines, redo, and swapping game boards.
Visual Feedback: Provides real-time updates on player moves, scores, and game status.
Backend
Game Logic: Implements core game functionalities such as ship placement, move validation, and win/loss determination.
Multiplayer Support: Allows players to connect via a client-server model, enabling both local and network-based multiplayer games.
Save and Load Feature: Supports saving the current game state and loading previously saved games.
AI Bot: Provides an option for a single-player mode where the player competes against an AI opponent.
Class Structure: Follows an object-oriented architecture with clearly defined classes for game components such as BattleshipGame, GameBoard, PowerButtons, LogPanel, and more.
Class Structure
Core Classes
BattleshipGame: Manages the overall game flow, including starting and ending the game, saving/loading, and switching players.
GameBoard: Handles board initialization, updates, and token placements.
LogPanel: Displays logs and messages during the game.
PowerButtons: Implements special actions like lifelines and redo functionality.
InfoPanel: Provides game status updates, including player information and turn indication.
Additional Features
Multiplayer: Manages the connection between players for network-based games.
SaveGame: Handles saving and loading game data to and from external files.
AI Bot: Provides logic for AI player moves.
Game Modes
Single-player Mode: Compete against an AI opponent.
Multiplayer Mode: Play against another player over the network or locally on the same machine.
Chat Feature: Players can send messages to each other during multiplayer sessions.
Networking
The game follows a client-server architecture. Messages are exchanged between players using specific formats to handle game moves, chat, and connection statuses.

Message Types
place:<playername>:<x coordinate>:<y coordinate>: Sends player ship placement.
move:<playername>:<x coordinate>:<y coordinate>:<result>: Sends move and its result (hit, miss, etc.).
sendMessage:<playername>:<message>: Sends a chat message.
disconnect:<playername>: Notifies the other player of a disconnection.
