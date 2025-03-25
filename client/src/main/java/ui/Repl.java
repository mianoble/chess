package ui;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {
    private final PreloginClient preloginClient;
//    private final PostloginClient postloginClient;
//    private final GameplayClient gameplayClient;

    public enum State {
        prelogin, // 1
        postlogin, // 2
        gameplay // 3
    };

    private State state;

    public Repl(String serverUrl) {
        preloginClient = new PreloginClient(serverUrl);
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
                    System.out.print(SET_TEXT_COLOR_BLUE + result);

                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.print(msg);
                }
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.println("\n>>>" + SET_TEXT_COLOR_GREEN);
    }
}
