package model;

import java.util.Collection;

public record ListRes (
        Collection<GameData> games) {
}
