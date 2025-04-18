package ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import chess.ChessGame;
import client.NotificationHandler;
import client.ServerFacade;
import com.sun.nio.sctp.Notification;
import websocket.commands.ConnectCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final ServerFacade server;

    private final PreloginClient preloginClient;
    private final PostloginClient postloginClient;
    private final GameplayClient gameplayClient;

    private int gameID;
    private ChessGame.TeamColor currentColor;
    private String currentUser;

    private final List<ServerMessage> receivedMessages = new ArrayList<>();

    public enum State {
        prelogin, // 1
        postlogin, // 2
        gameplay // 3
    };

    private State state;

    public Repl(String serverUrl) {
        server = new ServerFacade(serverUrl, this);
        preloginClient = new PreloginClient(server);
        postloginClient = new PostloginClient(server, this);
        gameplayClient = new GameplayClient(server, this);
        state = State.prelogin;
    }

    public void run() {
        System.out.println("Welcome to the game of CHESS. Sign in to start.");
        System.out.println(preloginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";

        while(!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            if(state == State.prelogin) {
                try {
                    result = preloginClient.eval(line);
                    System.out.println(SET_TEXT_COLOR_BLUE + result);
                    if (result.startsWith("loggedin")) {
                        currentUser = result.substring(9);
                        result = result.substring(0,8);
                        state = State.postlogin;
                    } else if (result.startsWith("registered")) {
                        currentUser = result.substring(11);
                        result = result.substring(0,10);
                        state = State.postlogin;
                    }

                } catch (Throwable e) {
                    var msg = e.getMessage();
                    System.out.print(msg);
                }
            }

            if (state == State.postlogin) {
                try {
                    result = postloginClient.eval(line);
                    if (result.startsWith("joinedgame WHITE")) {
                        currentColor = ChessGame.TeamColor.WHITE;
                        // get gameID
                        String idString = result.substring(16);
                        gameID = Integer.parseInt(idString);
                        result = result.substring(0, 16);

                        gameplayClient.connectToGame(gameID, currentColor, currentUser);
                    } else if (result.startsWith("joinedgame BLACK")) {
                        currentColor = ChessGame.TeamColor.BLACK;
                        // get gameID
                        String idString = result.substring(16);
                        gameID = Integer.parseInt(idString);
                        result = result.substring(0, 16);

                        gameplayClient.connectToGame(gameID, currentColor, currentUser);
//                        gameplayClient.initWebSocket(); // 👈 Make this public if needed
//                        server.getWebSocket().userJoinedAGame(server.getAuthID(), server.getUsername(), gameID,
//                                ConnectCommand.Role.PLAYER);
                    }
                    if (result.startsWith("spectating")) {
                        currentColor = ChessGame.TeamColor.WHITE;
                        // get gameID
                        String idString = result.substring(10);
                        gameID = Integer.parseInt(idString);
                        result = result.substring(0, 10);
                    }
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                    var board = result.split(" ");
                    if (result.equals("loggedout")) {
                        state = State.prelogin;
                    }
                    else if (board[0].equals("joinedgame") || result.equals("spectating")) {
                        state = State.gameplay;
                    }
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.println(msg);
                }
            }

            if (state == State.gameplay) {
                try {
                    result = gameplayClient.eval(line, gameID, currentColor, currentUser);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                    var board = result.split(" ");
                    if (board[0].equals("resignprompt")) {
                        System.out.println();
                        System.out.println("Are you sure you want to resign? <YES|NO>");
                    }
                    if (board.length < 2 && (board[0].equals("left"))) {
                        state = State.postlogin;
                    }
                    if (result.equals("invalidcolor")) {
                        System.out.println("Invalid player choice");
                        state = State.postlogin;
                    }
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.println(msg);
                }

            }
        }
        System.out.println();
    }

    public void notify(ServerMessage message) {
        receivedMessages.add(message);

        if (message instanceof NotificationMessage) {
            NotificationMessage nm = (NotificationMessage) message;
            System.out.println();
            System.out.println(SET_TEXT_COLOR_MAGENTA + nm.getMessage());
        } else if (message instanceof ErrorMessage) {
            ErrorMessage em = (ErrorMessage) message;
            System.out.println();
            System.out.println(SET_TEXT_COLOR_RED + "Error: " + em.getErrorMessage());
        } else if (message instanceof LoadGameMessage) {
            LoadGameMessage lm = (LoadGameMessage) message;
            System.out.println();
            System.out.println(SET_TEXT_COLOR_GREEN + "Board updated.");
            gameplayClient.getBoardPrintUpdater().boardUpdate(lm.getGame());
            gameplayClient.getBoardPrintUpdater().boardPrint(currentColor, null);
        } else {
            System.out.println();
            System.out.println(SET_TEXT_COLOR_RED + "[Unrecognized message type]");
        }

        printPrompt();
    }

    public List<ServerMessage> getReceivedMessages() {
        return receivedMessages;
    }

    private void printPrompt() {
        System.out.println("\n>>>" + SET_TEXT_COLOR_GREEN);
    }
}
