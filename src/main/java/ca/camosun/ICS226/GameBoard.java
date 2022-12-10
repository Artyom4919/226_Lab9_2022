package ca.camosun.ICS226;
import java.io.*;
import java.net.*;


class Gameboard {
        final int BOARD_SIZE = 4;
        final char BLANK = '_';
        final char[] PLAYERS = new char []{'1', '2', '3'};
        final int tokensInARowToWin = 4;
        char[][][] board = new char[BOARD_SIZE][BOARD_SIZE][BOARD_SIZE];
        int playerTurn = 1;
        boolean gameWon = false;
        
        //Gameboard constructor 
        public Gameboard() {
            initializeBoard();
        }
        

        //Initialize gameboard with BLANK spaces.
        public void initializeBoard() {
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int k = 0; k < BOARD_SIZE; k++) {
                    for (int j = 0; j < BOARD_SIZE; j++) {
                        board[i][k][j] = BLANK;
                    }
                }
            }
        }
        
       //Prints board 
        public String printBoard() { 
            StringBuilder string = new StringBuilder(100);
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int k = 0; k < BOARD_SIZE; k++) {
                    for (int j = 0; j < BOARD_SIZE; j++) {
                        string.append(board[i][k][j]);
                    }
                    string.append('\n');
                }
                string.append('\n');
            }
            if (gameWon == true) {
                string.append("Player " + playerTurn + " wins*");
            }else{
                string.append("Player " + playerTurn + "'s turn*");
            }
            return string.toString();
        }

        //Checks to see if a token can be placed. If successful, returns true. If not successful, returns false
        public boolean placeToken(int layer, int row, int column, int token) {
                if (layer >= BOARD_SIZE || row >= BOARD_SIZE || column >= BOARD_SIZE) {
                    return false;   //Check command locations to ensure they are withing range
                }
                if (token != Character.getNumericValue(PLAYERS[playerTurn-1])) { //Check to make sure the token being used is the proper token
                    return false;
                }else {
                    if (board[layer][row][column] == BLANK) {
                        char tokenChar =(char)(token + '0');
                        board[layer][row][column] = tokenChar;
                        check_for_win(token);
                        if (gameWon == false) {
                        playerTurn = playerTurn + 1;
                        }
                        if (playerTurn == 4) {
                            playerTurn = 1;
                        }
                        return true;
                    }else {
                        return false;
                    }
                }
        }
        //Resets game
        public void resetGame() {
            gameWon = false;
            playerTurn = 1;
        }
       //Checks for a win. If a game is won, gameWon is set to true and functionality for other functions are changed.
       public void check_for_win(int token) {
          int columnsToWin = 0;
          for (int layer = 0; layer < BOARD_SIZE; layer++) {
                for (int row = 0; row < BOARD_SIZE; row++) {
                    for (int column = 0; column < BOARD_SIZE; column++) {
                        if (Character.getNumericValue(board[layer][row][column]) == token) {
                              columnsToWin++;
                        }if (columnsToWin == tokensInARowToWin) {
                            gameWon = true;
                        }
                    }
                }
                columnsToWin = 0;
            }
       }
}