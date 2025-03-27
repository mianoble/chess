package ui;

import java.util.Scanner;
import facade.ServerFacade;
import static ui.EscapeSequences.*;

public class Repl {
    private final ServerFacade server;

    private final PreloginClient preloginClient;
    private final PostloginClient postloginClient;
    private final GameplayClient gameplayClient;

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
                    var msg = e.toString();
                    System.out.print(msg);
                }
            }

            if (state == State.postlogin) {
                try {
                    result = postloginClient.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                    var board = result.split(" ");
                    if (result.equals("loggedout")) {
                        state = State.prelogin;
                    }
                    else if (result.equals("newgame") || board[0].equals("joinedgame") || result.equals("spectating")) {
                        state = State.gameplay;
                    }
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.println(msg);
                }
            }

            if (state == State.gameplay) {
                try {
                    var board = result.split(" ");
                    if (board.length < 2) { //spectating only
                        gameplayClient.printBoardWhiteView();
                    }
                    else {
                        if (board[1].equals("WHITE")) { // white board
                            gameplayClient.printBoardWhiteView();
                        }
                        else { // black board
                            gameplayClient.printBoardBlackView();
                        }
                    }
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.println(msg);
                }
                // todo: go back to other states?
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.println("\n>>>" + SET_TEXT_COLOR_GREEN);
    }
}
