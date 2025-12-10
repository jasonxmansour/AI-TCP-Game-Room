import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out; 
    private BufferedReader in;
    private gameRoom currentRoom;
    private String playerName;
    
    
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }
    
    public String getPlayerName(){
    return this.playerName;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            showLoadingBar(out);
            promptPlayerName(out, in);
            showMenu(out);
            
           
            
          String choice = in.readLine();     // Listen for client's choice
            if (choice != null) {
                handleChoice(choice.trim(), out); 
            }
            
            gameRoom room = GameLobby.getInstance().getRoom(1);
            if (room.join(this)) {
                this.currentRoom = room; 
            } else {
                sendMessage("Unable to join room: " + room.getRoomName() + "\n Room full or already joined.");
            }
            
            String line;
            while ((line = in.readLine()) != null) {
                currentRoom.handlePlayerMessage(this, line);
            }
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
           cleanup();
        }
    }


    private void showLoadingBar(PrintWriter out) throws InterruptedException {
        out.println("\n\n");
        out.println("  ╔════════════════════════════════════════════════════════╗");
        out.println("  ║         CONNECTING TO GAME SERVER...                   ║");
        out.println("  ╚════════════════════════════════════════════════════════╝");
        out.println("");

        String[] frames = {
            "  [░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░] 0%",
            "  [████░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░] 10%",
            "  [████████░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░] 20%",
            "  [████████████░░░░░░░░░░░░░░░░░░░░░░░░░░░░] 30%",
            "  [████████████████░░░░░░░░░░░░░░░░░░░░░░░░] 40%",
            "  [████████████████████░░░░░░░░░░░░░░░░░░░░] 50%",
            "  [████████████████████████░░░░░░░░░░░░░░░░] 60%",
            "  [████████████████████████████░░░░░░░░░░░░] 70%",
            "  [████████████████████████████████░░░░░░░░] 80%",
            "  [████████████████████████████████████░░░░] 90%",
            "  [████████████████████████████████████████] 100%"
        };

        for (String frame : frames) {
            out.println(frame);
            Thread.sleep(200);
        }

        out.println("");
        out.println("  \u001B[32m✓ Connection Established!\u001B[0m");
        Thread.sleep(500);

        out.print("\033[2J");
        out.flush();
    }
    
    private void promptPlayerName( PrintWriter out, BufferedReader in ) throws IOException {
        out.println("\n════════════════════════════════════════════════════════════════════════════════");
        out.println("Please enter your player name:");
        out.println("════════════════════════════════════════════════════════════════════════════════");
        playerName = in.readLine(); 
        out.println("\nWelcome, " + playerName + "!\n");
    }

    private void showMenu(PrintWriter out) {
        out.println("  ██████╗  █████╗ ███╗   ███╗███████╗    ███╗   ███╗███████╗███╗   ██╗██╗   ██╗");
        out.println(" ██╔════╝ ██╔══██╗████╗ ████║██╔════╝    ████╗ ████║██╔════╝████╗  ██║██║   ██║");
        out.println(" ██║  ███╗███████║██╔████╔██║█████╗      ██╔████╔██║█████╗  ██╔██╗ ██║██║   ██║");
        out.println(" ██║   ██║██╔══██║██║╚██╔╝██║██╔══╝      ██║╚██╔╝██║██╔══╝  ██║╚██╗██║██║   ██║");
        out.println(" ╚██████╔╝██║  ██║██║ ╚═╝ ██║███████╗    ██║ ╚═╝ ██║███████╗██║ ╚████║╚██████╔╝");
        out.println("  ╚═════╝ ╚═╝  ╚═╝╚═╝     ╚═╝╚══════╝    ╚═╝     ╚═╝╚══════╝╚═╝  ╚═══╝ ╚═════╝");
        out.println("");
        out.println("════════════════════════════════════════════════════════════════════════════════");
        out.println("");
        out.println("                            SELECT YOUR GAME:");
        out.println("");
        out.println("════════════════════════════════════════════════════════════════════════════════");
        out.println("\u001B[32m");
        out.println("  1. ███╗   ███╗ █████╗ ████████╗██████╗ ██╗██╗  ██╗");
        out.println("     ████╗ ████║██╔══██╗╚══██╔══╝██╔══██╗██║╚██╗██╔╝");
        out.println("     ██╔████╔██║███████║   ██║   ██████╔╝██║ ╚███╔╝ ");
        out.println("     ██║╚██╔╝██║██╔══██║   ██║   ██╔══██╗██║ ██╔██╗ ");
        out.println("     ██║ ╚═╝ ██║██║  ██║   ██║   ██║  ██║██║██╔╝ ██╗");
        out.println("     ╚═╝     ╚═╝╚═╝  ╚═╝   ╚═╝   ╚═╝  ╚═╝╚═╚═╝  ╚═╝");
        out.println("\u001B[0m");
        out.println("");
        out.println("════════════════════════════════════════════════════════════════════════════════");
        out.println("");
        out.println("\u001B[34m");
        out.println("  2. ██████╗ ██████╗ ██╗███████╗ ██████╗ ███╗   ██╗    ██████╗ ██████╗ ███████╗ █████╗ ██╗  ██╗");
        out.println("     ██╔══██╗██╔══██╗██║██╔════╝██╔═══██╗████╗  ██║    ██╔══██╗██╔══██╗██╔════╝██╔══██╗██║ ██╔╝");
        out.println("     ██████╔╝██████╔╝██║███████╗██║   ██║██╔██╗ ██║    ██████╔╝██████╔╝█████╗  ███████║█████╔╝ ");
        out.println("     ██╔═══╝ ██╔══██╗██║╚════██║██║   ██║██║╚██╗██║    ██╔══██╗██╔══██╗██╔══╝  ██╔══██║██╔═██╗ ");
        out.println("     ██║     ██║  ██║██║███████║╚██████╔╝██║ ╚████║    ██████╔╝██║  ██║███████╗██║  ██║██║  ██╗");
        out.println("     ╚═╝     ╚═╝  ╚═╝╚═╝╚══════╝ ╚═════╝ ╚═╝  ╚═══╝    ╚═════╝ ╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝");
        out.println("\u001B[0m");
        out.println("");
        out.println("════════════════════════════════════════════════════════════════════════════════");
        out.println("");
        out.println("\u001B[31m");
        out.println("  3. ████████╗██████╗ ███████╗ █████╗ ███████╗██╗   ██╗██████╗ ███████╗    ██╗  ██╗██╗   ██╗███╗   ██╗████████╗");
        out.println("     ╚══██╔══╝██╔══██╗██╔════╝██╔══██╗██╔════╝██║   ██║██╔══██╗██╔════╝    ██║  ██║██║   ██║████╗  ██║╚══██╔══╝");
        out.println("        ██║   ██████╔╝█████╗  ███████║███████╗██║   ██║██████╔╝█████╗      ███████║██║   ██║██╔██╗ ██║   ██║   ");
        out.println("        ██║   ██╔══██╗██╔══╝  ██╔══██║╚════██║██║   ██║██╔══██╗██╔══╝      ██╔══██║██║   ██║██║╚██╗██║   ██║   ");
        out.println("        ██║   ██║  ██║███████╗██║  ██║███████║╚██████╔╝██║  ██║███████╗    ██║  ██║╚██████╔╝██║ ╚████║   ██║   ");
        out.println("        ╚═╝   ╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚══════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝    ╚═╝  ╚═╝ ╚═════╝ ╚═╝  ╚═══╝   ╚═╝   ");
        out.println("\u001B[0m");
        out.println("");
        out.println("════════════════════════════════════════════════════════════════════════════════");
        out.println("");
        out.println("Enter your choice (1-3):");
    }

    private void handleChoice(String choice, PrintWriter out) {
        out.println("\n════════════════════════════════════════════════════════════════════════════════");
        switch (choice) {
            case "1":
               
                break;
            case "2":
              
                break;
            case "3":
            
                break;
            default:
                out.println("Invalid choice! Please restart and pick 1, 2, or 3.");
                break;
        }
        out.println("════════════════════════════════════════════════════════════════════════════════");
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
    
    private void cleanup() {
        try {
            if (currentRoom != null) {
                currentRoom.leave(this);  // >>> ADDED: tell the room this player left
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
