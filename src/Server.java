import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(8080);
            System.out.println("Server started on port 8080...");

            while (true) {
                Socket client = server.accept();
                System.out.println("New client connected!");
                new Thread(new ClientHandler(client)).start(); // uses external class
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
