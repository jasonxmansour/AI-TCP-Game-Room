import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    Socket Socket;

    public Client() {
        try {
            Socket = new Socket("localhost", 8080);
            BufferedReader in = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
            PrintWriter out = new PrintWriter(Socket.getOutputStream(), true); 

            // thread to keep reading messages from server
            new Thread(() -> {
                String message;
                try {
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                    System.out.println("Disconnected from server.");
                } catch (IOException e) {
                    System.out.println("Connection closed.");
                }
            }).start();

            // added scanner input (for sending messages)
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                out.println(input);
            }

        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }

    public static void main(String[] args){ 
        new Client();
    }
}
