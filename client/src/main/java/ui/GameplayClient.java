package ui;

import dataaccess.ResponseException;
import facade.ServerFacade;

import java.util.Arrays;

public class GameplayClient {
    ServerFacade server;
    public GameplayClient(ServerFacade server) {
        this.server = server;
    }

    public String printBoardWhiteView() {
        // todo: print board for white player view and spectating
    }

    public String printBoardBlackView() {
        // todo: print board for black player view
    }
}
