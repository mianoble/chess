package model;

public record GameDataForListing (
        int gameID,
        String whiteUsername,
        String blackUsername,
        String gameName ) {
}
