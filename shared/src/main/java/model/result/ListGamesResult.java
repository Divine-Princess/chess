package model.result;

import model.data.GameData;
import java.util.Collection;

public record ListGamesResult(Collection<GameData> games) {
}
