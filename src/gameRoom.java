import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;

public class gameRoom {
     private int playerCount;
     private String roomName;
     // changed these to clienthandler instead of client
     private ClientHandler[] players;
     private final int ID;
     private boolean gameStarted = false;
     private FileWriter chatLogWriter;
     private FileReader chatLogReader;


     public gameRoom(int ID) {
         this.playerCount = 0;
         this.ID = ID;
         this.giveName();
         this.players = new ClientHandler[5];
         try {
            chatLogWriter = new FileWriter("chatLog"+ID+".txt", true);
            chatLogReader = new FileReader("chatLog"+ID+".txt");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
     }

     private void giveName() {
         if (this.ID==1) this.roomName = "Matrix";
         if (this.ID==2) this.roomName = "Prison Break";
         if (this.ID==3) this.roomName = "Treasure Hunt";
     }
     
     public boolean join(ClientHandler client) {
         if (this.playerCount==5 || gameStarted) return false;
        
         this.players[this.playerCount] = client;
         this.playerCount++;
         // system.out.print doesnt print to the server
         client.catchUp();
         broadcast(client.getPlayerName() + " joined the room. (" + playerCount + "/5)");
         return true;

     }


     // we need to add broadcast function

    public void broadcast(String message) {
        writeToFile(message);
        for (ClientHandler client : this.players) {
            if (client != null) {
                client.receiveMessage(message);
            }
            
        }
    }

    private void writeToFile(String message) {
        try {
            chatLogWriter.write(message+"\n");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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

    public FileReader getFileReader() {
        return this.chatLogReader;
    }


    //Clears chat log, will call this method when the game is over to prepare for a new game
    private void clearChatLog() {
        try {
            FileWriter clearWriter = new FileWriter("chatLog"+ID+".txt", false);
            clearWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
