package ui;

import model.ResponseException;
import client.ServerFacade;
import model.GameDataClient;
import model.JoinReq;
import model.ListRes;


import java.util.Arrays;
import java.util.HashMap;

public class PostloginClient {
    ServerFacade server;
    HashMap<Integer, Integer> gameNumbers = new HashMap<>();

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
        try {
            server.create(params[0]);
            System.out.println("You've created a new game called: " + params[0]);
            return "newgame";
        } catch (ResponseException e) {
            System.out.println("Invalid entry to create a game. Try again");
            return "failed";
        }
    }

    public String list () {
        try {
            ListRes res = server.list();
            int i = 1;
            for (GameDataClient game : res.games()) {
                gameNumbers.put(i, game.gameID());
                System.out.print(i + ": ");
                if (game.whiteUsername() == null) {

                }
                else if (game.blackUsername() == null) {

                }

                System.out.print(game.gameName() + " - (white player: " +
                        (game.whiteUsername() != null ? game.whiteUsername() : "empty") +
                        ", black player: " +
                        (game.blackUsername() != null ? game.blackUsername() : "empty") +
                        ") \n");
                i++;
            }
            return "listed";
        } catch (ResponseException e) {
            return "failed";
        }
    }

    public String join (String... params) {
        if (params.length != 2) {
            return "Type \"join\" and the game ID and your player color to join.\n";
        }
        int gameNum = 0;
        try {
            gameNum = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
//            System.out.println("Invalid game ID format!");
            return "Invalid game ID format!";
        }
        if (gameNum <= 0 || gameNum > gameNumbers.size()) {
            return "Invalid game ID. Type \"join\" and the game ID and your player color to join.\n";
        }
        int id = gameNumbers.get(gameNum);
        String tempAuth = "none";
        String playerColor = params[1].toUpperCase();
        JoinReq joinRequest = new JoinReq(tempAuth, playerColor, id);
        try {
            server.join(joinRequest);
            System.out.println("You've joined game " + params[0] + " as the " + playerColor.toLowerCase() + " player!");
            if (playerColor.equals("WHITE")) {
                return "joinedgame " + playerColor;
            }
            else if (playerColor.equals("BLACK")) {
                return "joinedgame " + playerColor;
            }
            else {
                return "failed";
            }
        } catch (ResponseException e) {
            return "failed";
        }
    }

    public String spectate (String... params) {
        if (params.length != 1) {
            return "Type \"spectate\" and the game ID to spectate.\n";
        }
        int gameNum = 0;
        try {
            gameNum = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            return "Invalid game ID format!";
        }
        if (gameNum <= 0 || gameNum > gameNumbers.size()) {
            return "Invalid game ID. Type \"spectate\" and the game ID to spectate.\n";
        }
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
