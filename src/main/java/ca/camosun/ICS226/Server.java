package ca.camosun.ICS226;
import java.io.*;
import java.net.*;

public class Server {
    final char[] PLAYERS = new char []{'1', '2', '3'};
    //Host string
    protected final String HOST = "";
    //End of input marker
    protected final String END_MARKER = "*";
    //Get command char
    protected final char GET_COMMAND = 'G';
    //Put command
    protected final char PUT_COMMAND = 'P';
    //Clear command
    protected final char CLEAR_COMMAND = 'C';
    //Message for successful put command
    protected final String SUCCESS_MESSAGE = "O";
    //Error message
    protected final String FAIL_MESSAGE = "E";
    //Rejection message
    protected final String REJECTION_MESSAGE = "R";
    protected final int MAX_PLAYERS = 3;
    protected final int COMMAND_LENGTH = 5;
    protected final int COMMAND_INDEX = 0;
    protected final int TOKEN_INDEX = 4;
    protected int port;
    protected int num_connections = 0;//track number of connections
    protected int clientID = 0; //Used to create unique id for each thread
    Gameboard board = new Gameboard(); //Create new gameboard at the beginning of a game. 
    public Server(int port) {
        this.port = port;
    }

    //Function to receive client, client id, and receive input. Interprets input and delgates the command to the relevant Gameboard functions. 
    void delegate(Socket clientSocket, int clientID) {
        
        try (
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            num_connections++;//
            if (num_connections > MAX_PLAYERS) {
                out.print(REJECTION_MESSAGE + END_MARKER); //Reject all connections after three connections (MAX_PLAYERS)
                out.flush();
            } else {
            while (true) {
                String inputLine = in.readLine();
                if (inputLine == null) {
                    break;
                }
                synchronized(this) {
                    
                    if (inputLine.equals("")) {
                        out.print(FAIL_MESSAGE + END_MARKER); //Return error if input is empty string is provided as input
                        out.flush();
                    } else {
                        if (inputLine.charAt(COMMAND_INDEX) == GET_COMMAND && inputLine.length() == 1) { //Get and print board if G command is received from client
                            out.print(board.printBoard());
                            out.flush(); 
                        }  else if (inputLine.charAt(COMMAND_INDEX) == CLEAR_COMMAND && inputLine.length() == 1) { //Clear  board if C command is received from client
                            out.print(SUCCESS_MESSAGE + END_MARKER);
                            out.flush();
                            board = new Gameboard();
                            board.resetGame();
                        }else if (inputLine.charAt(COMMAND_INDEX) == PUT_COMMAND && board.gameWon == false && inputLine.length() == COMMAND_LENGTH && inputLine.charAt(TOKEN_INDEX) == PLAYERS[clientID]) { //If a proper p Command is placed, process the input string and pass results as arguements to place_token
                            boolean tokenPlaced = false; 
                            int layer = Character.getNumericValue(inputLine.charAt(1));
                            int row = Character.getNumericValue(inputLine.charAt(2));
                            int column = Character.getNumericValue(inputLine.charAt(3));
                            int token = Character.getNumericValue(inputLine.charAt(4));
                            tokenPlaced = board.placeToken(layer,row,column,token);
                            if (tokenPlaced == true) {
                                out.print(SUCCESS_MESSAGE + END_MARKER); //If token is placed successfully
                                out.flush();
                            } else {
                                out.print(FAIL_MESSAGE + END_MARKER); //If token fails to place.
                                out.flush();
                            }
                        } else {
                            out.print(FAIL_MESSAGE + END_MARKER); // Triggers if command is invalid 
                            out.flush();
                        }
                    }
                }   
            }
        }
          clientSocket.close();
        } catch (Exception e) {
            System.err.println(e);
            System.exit(-1);
        }
    }

    public void serve() {
        
        try (
            ServerSocket serverSocket = new ServerSocket(port);
        ) {
           
            while(true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    Runnable runnable = () -> this.delegate(clientSocket, clientID++); //Send clientSocket and a new ClientID to delegate.
                    Thread t = new Thread(runnable);
                    t.start();
                } catch (Exception e) {
                    System.err.println(e);
                    System.exit(-2);
                }
            }
        } catch (Exception e) {
            System.err.println(e);
            System.exit(-3);
        }
    }
}