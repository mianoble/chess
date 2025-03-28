package ui;

import dataaccess.ResponseException;
import facade.ServerFacade;
import model.CreateRequest;
import model.CreateResult;
import model.JoinRequest;
import model.ListRequest;

import java.util.Arrays;

public class PostloginClient {
    ServerFacade server;
    public PostloginClient(ServerFacade server) {
        this.server = server;
    }

    public String eval (String input) {
        var tokens = input.split(" ");
        var cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch(cmd) {
            case "logout" -> logout();
            case "create" -> create(params);
            case "help" -> help();
            case "list" -> list();
            case "join" -> join(params);
            case "spectate" -> spectate(params);
            case "quit" -> "quit";
            default -> help();
        };
    }

    public String logout() {
        try {
            server.logout(); // todo: get TA help
            return "loggedout";
        } catch (ResponseException e) {
            System.out.println("Error occurred in logging out. Try again");
            return "failed";
        }
    }

    public String create (String... params) {
        if (params.length != 1) {
            return "Type \"create\" and the name of your new game.\n";
        }
        String tempAuth = "none";
        CreateRequest createRequest = new CreateRequest(tempAuth, params[0]); // todo: need help too
        try {
            CreateResult result = server.create(createRequest);
            System.out.println("You've created a new game! " + params[0]);
            return "newgame";
        } catch (ResponseException e) {
            System.out.println("Invalid entry to create a game. Try again");
            return "failed";
        }
    }

    public String list () {
        try {
            server.list();
            return "listed";
        } catch (ResponseException e) {
            return "failed";
        }
    }

    public String join (String... params) {
        if (params.length != 2) {
            return "Type \"join\" and the game ID and your player color to join.\n";
        }
        int id = 0;
        try {
            id = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid game ID format!");
        }
        String tempAuth = "none";
        JoinRequest joinRequest = new JoinRequest(tempAuth, params[1], id); // todo: need help too
        try {
            server.join(joinRequest);
            System.out.println("You've join this game! " + params[0]);
            if (params[1].equals("WHITE")) {
                return "joinedgame " + params[1];
            }
            else if (params[1].equals("BLACK")) {
                return "joinedgame " + params[1];
            }
            else {
                return "failed";
            }
        } catch (ResponseException e) {
            return "failed";
        }
    }

    public String spectate (String... params) {
        // todo: ask here
        return "spectating";
    }

    public String help() {
        return """
                What do you want to do?
                    Logout: type "logout"
                    Create a new game: type "create" <GAMENAME>
                    Show existing games: type "list"
                    Join an existing game: type "join" <GAMEID> [BLACK|WHITE]
                    Spectate a game: type "spectate" <GAMEID>
                    Print a list of possible actions: type "help"
                    Quit the game: type "quit"
                """;
    }
}
