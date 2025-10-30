
public class Commands {
    public static final String JOIN_LOBBY = "/join";
    public static final String SELECT_GAME = "/select";
    public static final String START_GAME = "/start";
    public static final String LEAVE_ROOM = "/leave";
    public static final String LIST_GAMES = "/games";
    public static final String LIST_PLAYERS = "/players";
    
    public static final String MATRIX_GAME = "matrix";
    public static final String PRISON_BREAK = "prison";
    public static final String TREASURE_HUNT = "treasure";
    
    public static final int MAX_PLAYERS_MATRIX = 5;
    public static final int MAX_PLAYERS_PRISON = 5;
    public static final int MAX_PLAYERS_TREASURE = 5;
    
    public static final String LOBBY_FULL = "Lobby is full.";
    public static final String GAME_STARTED = "Game has already started.";
    public static final String INVALID_GAME = "Invalid game selection. Use /games to see available games.";
    public static final String JOINED_LOBBY = "Welcome to the lobby! Use /games to see available games.";
    public static final String MUST_BE_IN_LOBBY = "You must be in a lobby to perform this action.";
    
    public static String processCommand(String command) {
        String[] parts = command.toLowerCase().trim().split("\\s+");
        
        switch (parts[0]) {
            case JOIN_LOBBY:
                return handleJoinLobby();
                
            case SELECT_GAME:
                if (parts.length < 2) {
                    return "Please specify a game: " + MATRIX_GAME + ", " + PRISON_BREAK + ", or " + TREASURE_HUNT;
                }
                return handleGameSelection(parts[1]);
                
            case START_GAME:
                return handleStartGame();
                
            case LIST_GAMES:
                return "Available games:\n" +
                       "1. " + MATRIX_GAME + " (2-" + MAX_PLAYERS_MATRIX + " players)\n" +
                       "2. " + PRISON_BREAK + " (2-" + MAX_PLAYERS_PRISON + " players)\n" +
                       "3. " + TREASURE_HUNT + " (2-" + MAX_PLAYERS_TREASURE + " players)";
                
            case LIST_PLAYERS:
                return handleListPlayers();
                
            case LEAVE_ROOM:
                return handleLeaveRoom();
                
            default:
                return "Unknown command. Available commands: " +
                       JOIN_LOBBY + ", " + SELECT_GAME + ", " + START_GAME + ", " +
                       LIST_GAMES + ", " + LIST_PLAYERS + ", " + LEAVE_ROOM;
        }
    }
    
    private static String handleJoinLobby() {
        return JOINED_LOBBY;
    }
    
    private static String handleGameSelection(String gameType) {
        switch (gameType) {
            case MATRIX_GAME:
            case PRISON_BREAK:
            case TREASURE_HUNT:
                return "Selected game: " + gameType;
            default:
                return INVALID_GAME;
        }
    }
    
    private static String handleStartGame() {
        return "Starting game...";
    }
    
    private static String handleListPlayers() {
        return "Player list will be displayed here";
    }
    
    private static String handleLeaveRoom() {
        return "Leaving room...";
    }
}
