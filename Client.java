import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    Socket Socket;

    public Client() {
        try {
            Socket = new Socket("localhost", 8080);
            BufferedReader in = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
                if (message.contains("Enter your choice")) {
                    break;
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }

    public static void main(String[] args){
        Client client = new Client();
    }
}