package ui;

import dataaccess.ResponseException;

import java.util.Arrays;
import facade.ServerFacade;
import model.LoginRequest;
import model.LoginResult;

public class PreloginClient {

    ServerFacade server;
    public PreloginClient(ServerFacade serverFacade) {
        this.server = serverFacade;
    }

    public String eval (String input) {
        try {
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "help" -> help();
                case "quite" -> "Quitting... goodbye.";
                default -> help();
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length != 2) {
            printLoginMessage();
        }
        if (params.length == 2) {
            LoginRequest request = new LoginRequest(params[0], params[1]);
            LoginResult result = server.login(request);

        }
    }

    private void printLoginMessage() {
        System.out.println("Type \"login\" and your username and password to log in.\n");
    }

    public String help() {
        return """
                What do you want to do?
                    Login as an existing user: type "login" <USERNAME> <PASSWORD>
                    Register as a new user: type "register" <USERNAME> <PASSWORD> <EMAIL>
                    Print a list of possible actions: type "help"
                    Quit the game: type "quit"
                """;
    }
}
