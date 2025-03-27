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
       