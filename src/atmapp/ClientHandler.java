package atmapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

public class ClientHandler extends Thread {
    
    private Socket socket;
    private ClientMain caller;
    private BufferedReader input;
    
    public ClientHandler(Socket socket, ClientMain caller) throws IOException {
        this.socket = socket;
        this.caller = caller;
        this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    }
    
    @Override
    public void run() {
        String message = " ";
        while(message != null && !message.equals("")) {
            try {
                message = this.input.readLine();
                StringTokenizer tok = new StringTokenizer(message, "|");
                double balance = Double.parseDouble(tok.nextToken());
                String log = tok.nextToken();
                caller.setBalance(balance);
                caller.addToLog(log);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    
}
