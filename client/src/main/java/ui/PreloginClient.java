package ui;

import dataaccess.ResponseException;

import java.util.Arrays;
import facade.ServerFacade;
import model.LoginRequest;
import model.LoginResult;
import model.RegisterRequest;
import model.RegisterResult;

public class PreloginClient {
    private String currentAuth;

    ServerFacade server;
    public PreloginClient(ServerFacade serverFacade) {
        this.server = serverFacade;
        currentAuth = "";
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
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length != 2) {
            return "Type \"login\" and your username and password to log in.\n";
        }
        LoginRequest request = new LoginRequest(params[0], params[1]);
        try {
            LoginResult result = server.login(request);
            System.out.println("You've signed in! Welcome " + params[0]);
            currentAuth = result.authToken();
            return "loggedin " + currentAuth;
        } catch (ResponseException e) {
            System.out.println("Incorrect username or password. Try again");
            return "failed";
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length != 3) {
            return "Type \"register\" and a username, password, and email to register.\n";
        }
        RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
        try {
            RegisterResult result = server.register(request);
            System.out.println("You've been registered! Welcome " + params[0]);
            currentAuth = result.authToken();
            return "registered " + currentAuth;
        } catch (ResponseException e) {
            System.out.println("Invalid username, password, or email. Try again");
            return "failed";
        }
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
