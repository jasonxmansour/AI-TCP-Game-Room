import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out; 
    private BufferedReader in;
    private gameRoom currentRoom;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            showLoadingBar(out);
            showMenu(out);

            String line;
            // persistent command loop — clients can send commands continuously
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0) continue;
                handleCommand(line, out);
                // if client closed or requested exit, the readLine will return null and loop ends
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

    private void handleCommand(String input, PrintWriter out) {
        String cmd = input.trim();
        String lower = cmd.toLowerCase();

        try {
            if (lower.startsWith("/join") || lower.startsWith("join")) {
                // allows: "/join room1", "join room1", "join 1", "join room 2"
                String[] parts = cmd.split("\\s+");
                if (parts.length < 2) {
                    out.println("Please specify a room, e.g. 'join room1' or 'join 1'.");
                    return;
                }
                String target = parts[1].toLowerCase();
                int roomId = -1;
                if (target.startsWith("room")) {
                    try { roomId = Integer.parseInt(target.replaceAll("[^0-9]", "")); } catch (NumberFormatException e) { roomId = -1; }
                } else {
                    try { roomId = Integer.parseInt(target); } catch (NumberFormatException e) { roomId = -1; }
                }

                if (roomId < 1 || roomId > 3) {
                    out.println("Invalid room. Use room1, room2, or room3.");
                    return;
                }

                gameRoom room = GameLobby.getInstance().getRoom(roomId);
                if (room == null) {
                    out.println("That room does not exist.");
                    return;
                }

                synchronized(room) {
                    if (room.isGameStarted()) {
                        out.println(Commands.GAME_STARTED);
                        return;
                    }
                    if (room.getPlayerCount() >= 5) {
                        out.println(Commands.LOBBY_FULL);
                        return;
                    }
                    boolean joined = room.join(this);
                    if (joined) {
                        this.currentRoom = room;
                        out.println("Joined room " + room.getRoomName() + ".");
                    } else {
                        out.println("Unable to join room.");
                    }
                }

                return;
            }

            if (lower.equals("/start") || lower.equals("start")) {
                if (this.currentRoom == null) {
                    out.println(Commands.MUST_BE_IN_LOBBY);
                    return;
                }
                if (this.currentRoom.isGameStarted()) {
                    out.println(Commands.GAME_STARTED);
                    return;
                }
                this.currentRoom.startGame();
                return;
            }

            if (lower.equals("/games") || lower.equals("games")) {
                out.println(Commands.processCommand("/games"));
                return;
            }

            if (lower.equals("/leave") || lower.equals("leave")) {
                if (this.currentRoom == null) {
                    out.println(Commands.MUST_BE_IN_LOBBY);
                    return;
                }
                this.currentRoom.leave(this);
                this.currentRoom = null;
                out.println("You left the room.");
                return;
            }

            // allow numeric menu selection (legacy)
            if (lower.equals("1") || lower.equals("2") || lower.equals("3")) {
                int requested = Integer.parseInt(lower);
                handleCommand("join " + requested, out);
                return;
            }

            // unknown command
            out.println("Invalid command. Available: /join room1, /start, /games, /leave.");
        } catch (Exception e) {
            out.println("An error occurred processing your command.");
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
    
    private void cleanup() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
