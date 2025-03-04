package model;

import java.util.Collection;

public record ListResult (
        Collection<GameDataForListing> allGames) {
}
