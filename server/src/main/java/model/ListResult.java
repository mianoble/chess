package model;

import java.util.Collection;

public record ListResult (
        Collection<GameData> games) {
}
