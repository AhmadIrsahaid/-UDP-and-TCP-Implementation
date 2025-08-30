import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class UDPServer {
    private static final int PORT = 8081;
    private static final int BUFFER_SIZE = 1024;
    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private static volatile boolean running = true;
    
    public static void main(String[] args) {
        System.out.println("Starting UDP Server on port " + PORT);
        
        try (DatagramSocket serverSocket = new DatagramSocket(PORT)) {
            System.out.println("UDP Server is listening on port " + PORT);
            System.out.println("Server IP: " + InetAddress.getLocalHost().getHostAddress());
            
            // Handle shutdown gracefully
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                running = false;
                executorService.shutdown();
                System.out.println("\nServer shutting down...");
            }));
            
            while (running) {
                try {
                    // Create buffer for incoming data
                    byte[] receiveBuffer = new byte[BUFFER_SIZE];
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    
                    // Wait for incoming packet
                    serverSocket.receive(receivePacket);
                    
                    // Handle packet in separate thread
                    executorService.submit(new PacketHandler(serverSocket, receivePacket));
                    
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error receiving packet: " + e.getMessage());
                    }
                }
            }
            
        } catch (SocketException e) {
            System.err.println("Could not create UDP socket: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            executorService.shutdown();
            try {
                executorService.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.err.println("Error during shutdown: " + e.getMessage());
            }
        }
    }
    
    static class PacketHandler implements Runnable {
        private final DatagramSocket serverSocket;
        private final DatagramPacket receivePacket;
        
        public PacketHandler(DatagramSocket socket, DatagramPacket packet) {
            this.serverSocket = socket;
            this.receivePacket = packet;
        }
        
        @Override
        public void run() {
            try {
                // Get client information
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                String clientInfo = clientAddress.getHostAddress() + ":" + clientPort;
                
                // Extract message from packet
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                
                System.out.println("Received from " + clientInfo + ": " + message);
                
                // Prepare response
                String response = "UDP Server received: " + message;
                byte[] responseBuffer = response.getBytes();
                
                // Create response packet
                DatagramPacket responsePacket = new DatagramPacket(
                    responseBuffer, 
                    responseBuffer.length, 
                    clientAddress, 
                    clientPort
                );
                
                // Send response back to client
                serverSocket.send(responsePacket);
                System.out.println("Sent response to " + clientInfo);
                
                // Special handling for quit command
                if ("quit".equalsIgnoreCase(message.trim())) {
                    String quitResponse = "Goodbye from UDP Server!";
                    byte[] quitBuffer = quitResponse.getBytes();
                    DatagramPacket quitPacket = new DatagramPacket(
                        quitBuffer, 
                        quitBuffer.length, 
                        clientAddress, 
                        clientPort
                    );
                    serverSocket.send(quitPacket);
                    System.out.println("Client " + clientInfo + " requested disconnect");
                }
                
            } catch (IOException e) {
                System.err.println("Error handling packet: " + e.getMessage());
            }
        }
    }
}
