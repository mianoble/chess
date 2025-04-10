package ui;

import java.util.Scanner;

import client.NotificationHandler;
import client.ServerFacade;
import com.sun.nio.sctp.Notification;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final ServerFacade server;

    private final PreloginClient preloginClient;
    private final PostloginClient postloginClient;
    private final GameplayClient gameplayClient;

    private int gameID;

    public enum State {
        prelogin, // 1
        postlogin, // 2
        gameplay // 3
    };

    private State state;

    public Repl(String serverUrl) {
        server = new ServerFacade(serverUrl);
        preloginClient = new PreloginClient(server);
        postloginClient = new PostloginClient(server);
        gameplayClient = new GameplayClient(server);
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
                    if (result.equals("loggedin") || result.equals("registered")) {
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
                    if (result.length() >= 10 && result.substring(0, 10).equals("joinedgame")) {
                        // get gameID
                        String idString = result.substring(16);
                        gameID = Integer.parseInt(idString);

                        result = result.substring(0, 16);
                    }

                    if (result.length() >= 10 && result.substring(0, 10).equals("spectating")) {
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
                    result = gameplayClient.eval(line, gameID);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                    var board = result.split(" ");
                    if (board.length < 2 && board[0].equals("exit")) { // spectating or quitting
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
        if (message instanceof NotificationMessage notificationMessage) {
            System.out.println(SET_TEXT_COLOR_RED + notificationMessage.getMessage());
        } else {
            System.out.println(SET_TEXT_COLOR_RED + "[Non-notification message]");
        }

        printPrompt();
    }

    private void printPrompt() {
        System.out.println("\n>>>" + SET_TEXT_COLOR_GREEN);
    }
}
