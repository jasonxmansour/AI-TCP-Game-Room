import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    Socket Socket;

    public Client() {
        try {
            Socket = new Socket("localhost", 8080);
            BufferedReader in = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
            PrintWriter out = new PrintWriter(Socket.getOutputStream(), true); // jasons printwriter

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
                if (message.contains("Enter your choice")) {
                    break;
                }
            }

            //added scanner input
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