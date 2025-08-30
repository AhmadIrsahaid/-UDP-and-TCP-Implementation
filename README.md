# Java Networking Project - UDP and TCP Implementation

This project demonstrates both UDP (User Datagram Protocol) and TCP (Transmission Control Protocol) networking implementations in Java. It includes client-server applications for both protocols, showcasing the differences in connection handling, reliability, and communication patterns.

## Project Structure

```
src/
├── Main.java          # TCP Server implementation
├── TCPClient.java     # TCP Client implementation
├── UDPServer.java     # UDP Server implementation
└── UDPClient.java     # UDP Client implementation
```

## Components Overview

### TCP Implementation

#### Main.java (TCP Server)
- **Port**: 8080
- **Protocol**: TCP (Transmission Control Protocol)
- **Features**:
  - Multi-threaded server that handles multiple clients simultaneously
  - Uses `ServerSocket` for accepting connections
  - Each client connection is handled in a separate thread via `ClientHandler`
  - Echo server functionality - sends back received messages
  - Graceful connection termination on "quit" command
  - Automatic client disconnection handling

#### TCPClient.java
- **Server**: localhost:8080
- **Protocol**: TCP
- **Features**:
  - Establishes persistent connection to TCP server
  - Bi-directional communication using `Socket`, `PrintWriter`, and `BufferedReader`
  - Separate thread for receiving server messages
  - Interactive console interface for sending messages
  - Graceful disconnection on "quit" command

### UDP Implementation

#### UDPServer.java
- **Port**: 8081
- **Protocol**: UDP (User Datagram Protocol)
- **Features**:
  - Connectionless server using `DatagramSocket`
  - Multi-threaded packet handling with `PacketHandler` class
  - Handles multiple clients simultaneously without maintaining connections
  - Echo functionality - sends back received messages
  - Special handling for "quit" command with goodbye message
  - Graceful shutdown with shutdown hook

#### UDPClient.java
- **Server**: localhost:8081
- **Protocol**: UDP
- **Features**:
  - Connectionless client using `DatagramSocket`
  - Packet-based communication with `DatagramPacket`
  - Separate thread for receiving server responses
  - 5-second timeout for receiving packets
  - Interactive console interface
  - Graceful disconnection handling

## Key Differences Between TCP and UDP

### TCP (Transmission Control Protocol)
- **Connection-oriented**: Establishes persistent connection
- **Reliable**: Guarantees message delivery and order
- **Stream-based**: Continuous data flow
- **Error handling**: Built-in error detection and retransmission
- **Use cases**: File transfer, web browsing, email

### UDP (User Datagram Protocol)
- **Connectionless**: No persistent connection
- **Unreliable**: No guarantee of delivery or order
- **Packet-based**: Individual datagrams
- **Lightweight**: Minimal overhead
- **Use cases**: Real-time applications, gaming, streaming

## How to Run

### Prerequisites
- Java 8 or higher
- No additional dependencies required

### Running the Applications

#### TCP Server and Client
1. **Start TCP Server**:
   ```bash
   javac src/Main.java
   java -cp src Main
   ```

2. **Start TCP Client** (in another terminal):
   ```bash
   javac src/TCPClient.java
   java -cp src TCPClient
   ```

#### UDP Server and Client
1. **Start UDP Server**:
   ```bash
   javac src/UDPServer.java
   java -cp src UDPServer
   ```

2. **Start UDP Client** (in another terminal):
   ```bash
   javac src/UDPClient.java
   java -cp src UDPClient
   ```

## Usage Instructions

### TCP Communication
1. Start the TCP server first
2. Start one or more TCP clients
3. Type messages in the client console
4. Server will echo back received messages
5. Type "quit" to disconnect

### UDP Communication
1. Start the UDP server first
2. Start one or more UDP clients
3. Type messages in the client console
4. Server will respond with acknowledgment
5. Type "quit" to disconnect

## Features

### Multi-threading
- Both servers support multiple concurrent clients
- Each client connection/packet is handled in separate threads
- Non-blocking I/O operations

### Error Handling
- Comprehensive exception handling for network errors
- Graceful shutdown procedures
- Connection timeout management (UDP)

### Interactive Interface
- Console-based user interface
- Real-time message display
- Clear client-server message distinction

### Protocol-Specific Features
- **TCP**: Persistent connections, reliable message delivery
- **UDP**: Connectionless communication, packet-based messaging

## Technical Details

### Network Configuration
- **TCP Server**: Port 8080
- **UDP Server**: Port 8081
- **Default Host**: localhost (127.0.0.1)
- **Buffer Size**: 1024 bytes (UDP)

### Threading Model
- **TCP Server**: Thread pool with cached executor service
- **UDP Server**: Thread pool with cached executor service
- **TCP Client**: Single background thread for receiving
- **UDP Client**: Single background thread with timeout

### Message Format
- **TCP**: Line-based text messages
- **UDP**: Byte array packets with string conversion

## Testing

You can test the applications by:
1. Running multiple clients simultaneously
2. Sending various message types
3. Testing disconnection scenarios
4. Observing protocol differences in behavior

## Notes

- The UDP implementation includes timeout handling to prevent infinite waiting
- Both implementations support graceful shutdown
- The TCP server can handle multiple clients concurrently
- The UDP server processes packets independently without maintaining client state
- All network operations include proper resource cleanup
