package atmapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class ServerHandler extends Thread {
    
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private Server caller;
    
    public ServerHandler(Socket socket, PrintWriter output, Server caller) throws IOException {
        this.socket = socket;
        this.output = output;
        this.caller = caller;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        newConnectionMessage();
    }
    
    @Override
    public void run() {
        String message = " ";
        while(this.socket.isConnected() && message != null && !message.equals("")) {
            try {
                processMessage();
                messageDispatcher();
            } catch(IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        try {
            close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public synchronized void newConnectionMessage() throws IOException {
        String message;
        List clients = this.caller.getClients();
        Iterator iterator = clients.iterator();
        PrintWriter output = null;
        message = caller.getBalance() + "|" + caller.getLog().concat("New connection successful: " + socket.toString());
        System.out.println(message);
        caller.clearLog();
        while(iterator.hasNext()) {
            output = (PrintWriter) iterator.next();
            output.println(message);
            output.flush();
        }
    }
    
    public synchronized void messageDispatcher() throws IOException {
        String message;
        List clients = this.caller.getClients();
        Iterator iterator = clients.iterator();
        PrintWriter output = null;
        message = caller.getBalance() + "|" + caller.getLog();
        System.out.println(message);
        caller.clearLog();
        while(iterator.hasNext()) {
            output = (PrintWriter) iterator.next();
            output.println(message);
            output.flush();
        }
    }
    
    private void processMessage() throws IOException {
            String message = input.readLine();
            if(message != null && !message.equals("")) {
                System.out.println("Thread [" + this.toString() + "] has received a message: " + message);
                StringTokenizer tok = new StringTokenizer(message, "|");
                int operation = Integer.parseInt(tok.nextToken());
                double amount = Double.parseDouble(tok.nextToken());
                caller.calculate(operation, amount);
            }
        }
    
    private synchronized void sendMessage(String message) {
        output.println(message);
    }
    
    @Override
    protected void finalize() throws Throwable {
        close();
    }

    private void close() throws Exception {
        this.input.close();
        this.caller.removeClient(this.output);
        this.socket.close();
    }
}
