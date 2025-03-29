package ui;

import dataaccess.ResponseException;
import facade.ServerFacade;
import static ui.EscapeSequences.*;

import java.io.PrintStream;
import java.util.Arrays;

public class GameplayClient {
    ServerFacade server;
    public GameplayClient(ServerFacade server) {
        this.server = server;
    }

    public String printBoardWhiteView() {
        // todo: print board for white player view and spectating
        System.out.println("temporary white board print");
        
        return "temp whiteboard!!";
    }

    public String printBoardBlackView() {
        // todo: print board for black player view
        System.out.println("temporary black board print");
        return "temp blackboard!!";
    }

    private static void drawHeaders(PrintStream out) {
        String[] cols = {"1", "2", "3", "4,", "5", "6", "7", "8"};
        String[] rows = {"a", "b", "c", "d", "e", "f", "g", "h"};


    }

    private static void printHeaderText(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }
}