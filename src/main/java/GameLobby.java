public class GameLobby {
    private static GameLobby instance;
    private gameRoom[] rooms;
    
    private GameLobby() {
        rooms = new gameRoom[4];
        rooms[1] = new gameRoom(1);
        rooms[2] = new gameRoom(2);
        rooms[3] = new gameRoom(3);
    }
    
    public static synchronized GameLobby getInstance() {
        if (instance == null) {
            instance = new GameLobby();
        }
        return instance;
    }
    
    public gameRoom getRoom(int roomID) {
        if (roomID >= 1 && roomID <= 3) {
            return rooms[roomID];
        }
        return null;
    }
}