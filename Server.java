import java.io.*;
import java.net.*;

public class Server{
    public static void main(String[] args){
        try {
            ServerSocket Server = new ServerSocket(8080);
            Socket S = Server.accept();
            PrintWriter out = new PrintWriter(S.getOutputStream(), true);
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
            out.println("     ╚═╝     ╚═╝╚═╝  ╚═╝   ╚═╝   ╚═╝  ╚═╝╚═╝╚═╝  ╚═╝");
            out.println("\u001B[0m");
            out.println("════════════════════════════════════════════════════════════════════════════════");
            out.println("");
            out.println("Enter your choice (1-3):");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}