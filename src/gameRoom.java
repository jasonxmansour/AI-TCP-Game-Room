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
     
     public void broadcast(String message) {
     for (int i = 0; i < playerCount; i++) {
         if (players[i] != null) {
             players[i].sendMessage(message);
         }
     }
 }
     public boolean join(ClientHandler client) {
         if (this.playerCount==5) return false;

         this.players[this.playerCount] = client;
         this.playerCount++;
         // system.out.print doesnt print to the server
         broadcast(client.getPlayerName() + " joined the room. (" + playerCount + "/5)");
         return true;

     }
     // we need to add broadcast function 
     
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
