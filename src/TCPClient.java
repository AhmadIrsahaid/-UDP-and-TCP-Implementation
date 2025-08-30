import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TCPClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    
    public static void main(String[] args) {
        System.out.println("TCP Client starting...");
        System.out.println("Connecting to server at " + SERVER_HOST + ":" + SERVER_PORT);
        
        try (
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Connected to server successfully!");
            System.out.println("Type messages to send to server (type 'quit' to exit):");
            
            // Start a separate thread to receive messages from server
            Thread receiveThread = new Thread(() -> {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        System.out.println("Server: " + response);
                    }
                } catch (IOException e) {
                    if (!socket.isClosed()) {
                        System.err.println("Error receiving from server: " + e.getMessage());
                    }
                }
            });
            receiveThread.start();
            
            // Main thread for sending messages
            String userInput;
            while (true) {
                System.out.print("You: ");
                userInput = scanner.nextLine();
                
                if (userInput == null || userInput.trim().isEmpty()) {
                    continue;
                }
                
                out.println(userInput);
                
                if ("quit".equalsIgnoreCase(userInput.trim())) {
                    System.out.println("Disconnecting from server...");
                    break;
                }
            }
            
        } catch (UnknownHostException e) {
            System.err.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
        
        System.out.println("Client disconnected.");
    }
}
