public class gameRoom {
     private int playerCount;
     private String roomName;
     private Client[] players;
     private final int ID;


     public gameRoom(int ID) {
         this.playerCount = 0;
         this.ID = ID;
         this.giveName();
         this.players = new Client[5];
     }

     public void giveName() {
         if (this.ID==1) this.roomName = "Matrix";
         if (this.ID==2) this.roomName = "Prison Break";
         if (this.ID==3) this.roomName = "Treasure Hunt";
     }

     public boolean join(Client client) {
         if (this.playerCount==5) return false;

         this.players[this.playerCount] = client;
         this.playerCount++;
         System.out.println("Joined room "+ this.roomName + ".");
         return true;

     }


}
