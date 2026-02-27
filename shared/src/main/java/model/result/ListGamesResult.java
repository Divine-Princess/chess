package model.result;

import model.data.GameData;
import java.util.Collection;
import java.util.HashMap;

public record ListGamesResult(Collection<HashMap<String, GameData>> games) {
}
