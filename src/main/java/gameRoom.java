import java.util.ArrayList;
import java.util.List;

public class gameRoom {
    private int playerCount;
    private String roomName;
    private final List<ClientHandler> players = new ArrayList<>();
    private final int ID;
    private boolean gameStarted = false;
    private GameController gameController;

    public gameRoom(int ID) {
        this.playerCount = 0;
        this.ID = ID;
        this.giveName();
    }

    private void giveName() {
        if (this.ID == 1) this.roomName = "Matrix";
        if (this.ID == 2) this.roomName = "Prison Break";
        if (this.ID == 3) this.roomName = "Treasure Hunt";
    }
    
    public boolean join(ClientHandler client) {
        if (this.playerCount == 5 || gameStarted) return false;

        this.players.add(client);
        this.playerCount++;
        broadcast(client.getPlayerName() + " joined the room. (" + playerCount + "/5)");
        return true;
    }

    public void leave(ClientHandler client) {
        if (players.remove(client)) {                      
            if (playerCount > 0) {
                playerCount--;
            }
            broadcast(client.getPlayerName() + " left the room. (" + playerCount + "/5)");
        }
    }

    public void broadcast(String message) {
        for (ClientHandler client : this.players) {
            if (client != null) {
                client.sendMessage(message);
            }
        }
    }

    public void handlePlayerMessage(ClientHandler sender, String message) {
        if (message.trim().equalsIgnoreCase("/leave") || message.trim().equalsIgnoreCase("/quit")) {
            sender.sendMessage("You have left the room");
            sender.leaveRoomAndDisconnect(); // remove from room + close socket
            return;
        }
        if (message.trim().equalsIgnoreCase("/start")) {
            if (!gameStarted && playerCount >=1) {
                gameStarted = true;
                broadcast("Game starting! " + sender.getPlayerName() + " initiated the game.");
                this.gameController = new GameController(this, this.roomName);
            } else if (gameStarted) {
                sender.sendMessage("Game already started!");
            } else {
                sender.sendMessage("Need at least 2 players to start. Current: " + playerCount);
            }
        } else if (message.trim().toLowerCase().startsWith("/vote ") && gameStarted && gameController != null) {
            try {
                int choice = Integer.parseInt(message.trim().substring(6));
                gameController.handleVote(sender.getPlayerName(), choice);
            } catch (NumberFormatException e) {
                sender.sendMessage("Usage: /vote [1-4]");
            }
        } else if (!gameStarted) {
            broadcast(sender.getPlayerName() + ": " + message);
        }
    }
    
    public void sendToPlayer(String playerName, String message) {
        for (ClientHandler client : players) {
            if (client != null && client.getPlayerName() != null && 
                client.getPlayerName().equals(playerName)) {
                client.sendMessage(message);
                break;
            }
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

    public void setGameController(GameController controller) {
        this.gameController = controller;
    }
}