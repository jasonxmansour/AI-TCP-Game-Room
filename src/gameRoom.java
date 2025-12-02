public class gameRoom {
     private int playerCount;
     private String roomName;
     // changed these to clienthandler instead of client
     private ClientHandler[] players;
     private final int ID;
     private boolean gameStarted = false;


     public gameRoom(int ID) {
         this.playerCount = 0;
         this.ID = ID;
         this.giveName();
         this.players = new ClientHandler[5];
     }

     public void giveName() {
         if (this.ID==1) this.roomName = "Matrix";
         if (this.ID==2) this.roomName = "Prison Break";
         if (this.ID==3) this.roomName = "Treasure Hunt";
     }

     public boolean join(ClientHandler client) {
        synchronized(this) {
            if (this.gameStarted) return false;
            if (this.playerCount == this.players.length) return false;

            // prevent duplicate joins
            for (int i = 0; i < this.players.length; i++) {
                if (this.players[i] == client) {
                    return false; // already in room
                }
            }

            this.players[this.playerCount] = client;
            this.playerCount++;
            broadcast("Player joined room " + this.roomName + ". (" + playerCount + "/" + this.players.length + ")");

            if (this.playerCount == this.players.length) {
                // auto-start when full
                startGame();
            }

            return true;
        }

     }
    // broadcast to all players currently in the room
    public void broadcast(String message) {
        synchronized(this) {
            for (int i = 0; i < players.length; i++) {
                ClientHandler p = players[i];
                if (p != null) {
                    p.sendMessage(message);
                }
            }
        }
    }

    // start the game in this room (not reversible)
    public void startGame() {
        synchronized(this) {
            if (gameStarted) return;
            gameStarted = true;
            broadcast("\\n════════════════════════════════════════════════════════════════════════════════");
            broadcast("Game in room '" + this.roomName + "' is starting now!");
            broadcast("Players: " + this.playerCount);
            broadcast("Use in-game commands as provided by the server.");
            broadcast("════════════════════════════════════════════════════════════════════════════════\\n");
        }
    }

    // remove a player from the room
    public void leave(ClientHandler client) {
        synchronized(this) {
            int idx = -1;
            for (int i = 0; i < players.length; i++) {
                if (players[i] == client) {
                    idx = i;
                    break;
                }
            }
            if (idx == -1) return;

            // shift left
            for (int i = idx; i < players.length - 1; i++) {
                players[i] = players[i+1];
            }
            players[players.length - 1] = null;
            playerCount--;
            broadcast("Player left room " + this.roomName + ". (" + playerCount + "/" + this.players.length + ")");
        }
    }
     
     public boolean isGameStarted() {  
        return gameStarted;
    }

    public String getRoomName() {  
        return roomName;
    }

    public int getPlayerCount() {  
        return playerCount;
    }


}
