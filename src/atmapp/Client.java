package atmapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    
    private Socket socket;
    private ClientHandler handler;
    private PrintWriter output;
    private BufferedReader input;
    
    public Client(String serverAddress, int serverPort, ClientMain caller) throws UnknownHostException, IOException {
        this.socket = new Socket(serverAddress, serverPort);
        this.socket.setKeepAlive(true);
        this.handler = new ClientHandler(socket, caller);
        this.handler.start();
        this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.output = new PrintWriter(this.socket.getOutputStream(), true);
    }
    
    public String readMessage() throws IOException {
        return this.input.readLine();
    }
    
    public synchronized void writeMessage(String outMessage) {
        this.output.println(outMessage);
    }
    
    public void closeConnection() throws IOException {
        this.handler.stop();
        this.input.close();
        this.output.close();
        this.socket.close();
    }
    
    @Override
    public void finalize() throws Throwable {
        try {
            this.closeConnection();
        } finally {
            super.finalize();
        }
    }
}
