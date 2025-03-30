package model;

import java.util.Collection;

public record ListRes (
        Collection<GameDataClient> games) {
}
