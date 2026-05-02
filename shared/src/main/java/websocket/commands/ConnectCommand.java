package websocket.commands;

public class ConnectCommand extends UserGameCommand {
    private final String playerColor;
    private final String username;

    public ConnectCommand(CommandType commandType, String authToken,
                          Integer gameID, String playerColor, String username) {
        super(commandType, authToken, gameID);
        this.playerColor = playerColor;
        this.username = username;
    }

    public String getPlayerColor() { return playerColor; }

    public String getUsername() { return username; }
}
