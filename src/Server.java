import java.io.*;
import java.net.*;

public class Server{
    public static void main(String[] args){
        try {
            ServerSocket server = new ServerSocket(8080);
            System.out.println("Server started on port 8080...");

            while(true) {
                Socket client = server.accept();
                System.out.println("New client connected!");
                new Thread(new ClientHandler(client)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                showLoadingBar(out);
                showMenu(out);

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        private void showLoadingBar(PrintWriter out) throws InterruptedException {
            out.println("\n\n");
            out.println("  ╔════════════════════════════════════════════════════════╗");
            out.println("  ║         CONNECTING TO GAME SERVER...                  ║");
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
            out.println("  2.  ██████╗  █████╗ ███╗   ███╗███████╗    ████████╗██╗    ██╗ ██████╗ ");
            out.println("     ██╔════╝ ██╔══██╗████╗ ████║██╔════╝    ╚══██╔══╝██║    ██║██╔═══██╗");
            out.println("     ██║  ███╗███████║██╔████╔██║█████╗         ██║   ██║ █╗ ██║██║   ██║");
            out.println("     ██║   ██║██╔══██║██║╚██╔╝██║██╔══╝         ██║   ██║███╗██║██║   ██║");
            out.println("     ╚██████╔╝██║  ██║██║ ╚═╝ ██║███████╗       ██║   ╚███╔███╔╝╚██████╔╝");
            out.println("      ╚═════╝ ╚═╝  ╚═╝╚═╝     ╚═╝╚══════╝       ╚═╝    ╚══╝╚══╝  ╚═════╝ ");
            out.println("\u001B[0m");
            out.println("");
            out.println("════════════════════════════════════════════════════════════════════════════════");
            out.println("");
            out.println("\u001B[31m");
            out.println("  3.  ██████╗  █████╗ ███╗   ███╗███████╗    ████████╗██╗  ██╗██████╗ ███████╗███████╗");
            out.println("     ██╔════╝ ██╔══██╗████╗ ████║██╔════╝    ╚══██╔══╝██║  ██║██╔══██╗██╔════╝██╔════╝");
            out.println("     ██║  ███╗███████║██╔████╔██║█████╗         ██║   ███████║██████╔╝█████╗  █████╗  ");
            out.println("     ██║   ██║██╔══██║██║╚██╔╝██║██╔══╝         ██║   ██╔══██║██╔══██╗██╔══╝  ██╔══╝  ");
            out.println("     ╚██████╔╝██║  ██║██║ ╚═╝ ██║███████╗       ██║   ██║  ██║██║  ██║███████╗███████╗");
            out.println("      ╚═════╝ ╚═╝  ╚═╝╚═╝     ╚═╝╚══════╝       ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝╚══════╝");
            out.println("\u001B[0m");
            out.println("");
            out.println("════════════════════════════════════════════════════════════════════════════════");
            out.println("");
            out.println("Enter your choice (1-3):");
        }
    }
}