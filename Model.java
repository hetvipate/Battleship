package CST8221;

import java.io.Serializable;
import java.util.Stack;

public class Model implements Serializable {
    private static final long serialVersionUID = 1L;

    private char[][] board;
    private int[][] directionBoard;
    private int player = 1;
    private char[] array = { ' ', '1', '2' };
    private char[] array2 = { ' ', 'A', 'B' };
    private char[] array3 = { ' ', 'a', 'b' };
    private boolean lifelineActivated = false;
    private boolean lifelineUsed = false; // Flag to track if lifeline has been used
    private int player1Score = 0;
    private int player2Score = 0;
    private boolean player1RedoUsed = false; // Track if Player 1 has used redo
    private boolean player2RedoUsed = false; // Track if Player 2 has used redo
    private boolean isPlayerView = true; // State to track which board is being viewed

    // Stack to keep track of moves for undo functionality
    private Stack<Move> moveHistory = new Stack<>();
    private Stack<Move> redoStack = new Stack<>(); // Stack to keep track of undone moves for redo functionality

    public Model(int size) {
        board = new char[size][size];
        directionBoard = new int[size][size];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                board[x][y] = '0';
            }
        }
    }

    public int makeMove(int x, int y) {
        if (((board[x][y] == '1' || board[x][y] == 'a' || board[x][y] == 'A') && player == 1)
                || ((board[x][y] == '2' || board[x][y] == 'b' || board[x][y] == 'B') && player == 2)
                || board[x][y] == 'c' || board[x][y] == 'd'|| board[x][y] == 'C' || board[x][y] == 'D') {
            return 0;
        }
        moveHistory.push(new Move(player, x, y, board[x][y])); // Save the current state before making a move
        redoStack.clear(); // Clear the redo stack whenever a new move is made
        if (board[x][y] == '0') {
        	
            board[x][y] = (player == 1 ? 'c' : 'C');
            return 1;
        }
        board[x][y] = (player == 1 ? 'd' : 'D');
        if (player == 1) {
        	player1Score++;
        }else
        	player2Score++;
        checkShipDestroyed(player == 1 ? 2 : 1);
        return 2;
    }

    public void undoLastMove() {
        if (!moveHistory.isEmpty()) {
            Move lastMove = moveHistory.pop();
            redoStack.push(lastMove); // Save the undone move for redo
            board[lastMove.getX()][lastMove.getY()] = lastMove.getPreviousState();
        }
    }

    public void redoLastMove() {
        if (!redoStack.isEmpty()) {
            Move redoMove = redoStack.pop();
            moveHistory.push(new Move(player, redoMove.getX(), redoMove.getY(), board[redoMove.getX()][redoMove.getY()]));
            board[redoMove.getX()][redoMove.getY()] = (player == 1) ? 'd' : 'c'; // Assuming '4' and '3' are hit and miss
        }
    }

    public int getPlayer() {
        return player;
    }

    public void nextPlayer() {
        player = (player == 1) ? 2 : 1;
    }

    public void setCurrentPlayer(int player) {
        this.player = player;
    }

    public boolean tryPlaceShip(int player, int length, int x, int y, boolean horizontal) {
        if (horizontal) {
            if (x + length > board.length)
                return false;
            for (int i = 0; i < length; i++) {
                if (board[x + i][y] != '0')
                    return false;
            }
            board[x][y] = array2[player];
            for (int i = 1; i < length - 1; i++) {
                board[x + i][y] = array[player];
            }
            board[x + length - 1][y] = array3[player];
        } else {
            if (y + length > board[0].length)
                return false;
            for (int i = 0; i < length; i++) {
                if (board[x][y + i] != '0')
                    return false;
            }
            board[x][y] = array2[player];
            directionBoard[x][y] = 1;
            for (int i = 1; i < length - 1; i++) {
                board[x][y + i] = array[player];
            }
            board[x][y + length - 1] = array3[player];
            directionBoard[x][y + length - 1] = 1;
        }
        return true;
    }

    public char get(int x, int y) {
        return board[x][y];
    }

    public int getDirection(int x, int y) {
        return directionBoard[x][y];
    }

    public void set(int x, int y, char c) {
        board[x][y] = c;
    }

    public int getSize() {
        return board.length;
    }

    public boolean isGameOver() {
        int player1Ships = 0, player2Ships = 0;
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board.length; x++) {
                if (board[x][y] == '1') {
                    player1Ships++;
                }
                if (board[x][y] == 'a') {
                    player1Ships++;
                }
                if (board[x][y] == 'A') {
                    player1Ships++;
                }

                if (board[x][y] == '2') {
                    player2Ships++;
                }
                if (board[x][y] == 'b') {
                    player2Ships++;
                }
                if (board[x][y] == 'B') {
                    player2Ships++;
                }
            }
        }
        return player1Ships == 0 || player2Ships == 0;
    }

    public char[][] getBoard() {
        return board;
    }

    private void checkShipDestroyed(int opponent) {
        boolean shipDestroyed = true;
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board.length; x++) {
                if (opponent == 1) {
                    if (board[x][y] == '1' || board[x][y] == 'a' || board[x][y] == 'A') {
                        shipDestroyed = false;
                        break;
                    }
                }
                if (opponent == 2) {
                    if (board[x][y] == '2' || board[x][y] == 'b' || board[x][y] == 'B') {
                        shipDestroyed = false;
                        break;
                    }
                }
            }
            if (!shipDestroyed)
                break;
        }
        if (shipDestroyed) {
            if (opponent == 1) {
                player2Score++;
            } else if (opponent == 2) {
                player1Score++;
            }
        }
        if (shipDestroyed && !lifelineActivated) {
            lifelineActivated = true;
        }
    }

    public boolean isLifelineActivated() {
        return lifelineActivated;
    }

    public void useLifeline() {
        lifelineActivated = false;
        lifelineUsed = true; // Set flag to indicate lifeline has been used
    }

    public boolean isLifelineUsed() {
        return lifelineUsed;
    }

    public void swapBoards() {
        isPlayerView = !isPlayerView;
    }

    public boolean isPlayerView() {
        return isPlayerView;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public void activateLifeline() {
        lifelineActivated = true;
    }

    public void deactivateLifeline() {
        lifelineActivated = false;
        lifelineUsed = true;
    }

    public boolean isPlayer1RedoUsed() {
        return player1RedoUsed;
    }

    public void setPlayer1RedoUsed(boolean player1RedoUsed) {
        this.player1RedoUsed = player1RedoUsed;
    }

    public boolean isPlayer2RedoUsed() {
        return player2RedoUsed;
    }

    public void setPlayer2RedoUsed(boolean player2RedoUsed) {
        this.player2RedoUsed = player2RedoUsed;
    }

    private static class Move implements Serializable {
        private static final long serialVersionUID = 1L;
        private final int player;
        private final int x;
        private final int y;
        private final char previousState;

        public Move(int player, int x, int y, char previousState) {
            this.player = player;
            this.x = x;
            this.y = y;
            this.previousState = previousState;
        }

        public int getPlayer() {
            return player;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public char getPreviousState() {
            return previousState;
        }
    }
}
