import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.*;

public class UDPClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8081;
    private static final int BUFFER_SIZE = 1024;
    private static final int TIMEOUT = 5000; // 5 seconds timeout
    
    public static void main(String[] args) {
        System.out.println("UDP Client starting...");
        System.out.println("Connecting to server at " + SERVER_HOST + ":" + SERVER_PORT);
        
        try (
            DatagramSocket clientSocket = new DatagramSocket();
            Scanner scanner = new Scanner(System.in)
        ) {
            // Set timeout for receiving packets
            clientSocket.setSoTimeout(TIMEOUT);
            
            // Get server address
            InetAddress serverAddress = InetAddress.getByName(SERVER_HOST);
            
            System.out.println("UDP Client ready!");
            System.out.println("Type messages to send to server (type 'quit' to exit):");
            
            // Start a separate thread to receive messages from server
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<?> receiveTask = executorService.submit(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        // Create buffer for incoming data
                        byte[] receiveBuffer = new byte[BUFFER_SIZE];
                        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                        
                        try {
                            // Wait for incoming packet
                            clientSocket.receive(receivePacket);
                            
                            // Extract message from packet
                            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                            System.out.println("Server: " + response);
                            
                            // Check for quit response
                            if (response.contains("Goodbye")) {
                                System.out.println("Server acknowledged disconnect");
                                break;
                            }
                            
                        } catch (SocketTimeoutException e) {
                            // Timeout is expected, continue listening
                            continue;
                        }
                    }
                } catch (IOException e) {
                    if (!clientSocket.isClosed()) {
                        System.err.println("Error receiving from server: " + e.getMessage());
                    }
                }
            });
            
            // Main thread for sending messages
            String userInput;
            while (true) {
                System.out.print("You: ");
                userInput = scanner.nextLine();
                
                if (userInput == null || userInput.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    // Prepare message
                    byte[] sendBuffer = userInput.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(
                        sendBuffer, 
                        sendBuffer.length, 
                        serverAddress, 
                        SERVER_PORT
                    );
                    
                    // Send packet
                    clientSocket.send(sendPacket);
                    System.out.println("Message sent to server");
                    
                    // Check for quit command
                    if ("quit".equalsIgnoreCase(userInput.trim())) {
                        System.out.println("Disconnecting from server...");
                        break;
                    }
                    
                } catch (IOException e) {
                    System.err.println("Error sending message: " + e.getMessage());
                }
            }
            
            // Cleanup
            receiveTask.cancel(true);
            executorService.shutdown();
            try {
                executorService.awaitTermination(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.err.println("Error during cleanup: " + e.getMessage());
            }
            
        } catch (UnknownHostException e) {
            System.err.println("Server not found: " + e.getMessage());
        } catch (SocketException e) {
            System.err.println("Could not create UDP socket: " + e.getMessage());
        }
        
        System.out.println("UDP Client disconnected.");
    }
}
