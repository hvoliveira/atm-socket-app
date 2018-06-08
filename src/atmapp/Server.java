package atmapp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    
    private ServerMain caller;
    private List clients;
    private ServerSocket server;
    private double balance;
    private StringBuilder log;
    
    public synchronized void calculate(int operation, double amount) {
        switch(operation) {
            case 0 :
                this.balance = this.balance + amount;
                this.log.append("Deposit successful");
                break;
            case 1 :
                if(this.balance - amount >= 0.0) {
                    this.balance = this.balance - amount;
                    this.log.append("Withdrawal successful");
                } else {
                    this.log.append("Insufficient funds!");
                }
                break;
            default :
                this.log.append("Invalid operation");
                break;
        }
        caller.setBalance(this.balance);
    }
    
    public double getBalance() {
        return balance;
    }
    
    public Server(int port, ServerMain caller) throws IOException {
        this.caller = caller;
        this.server = new ServerSocket(port);
        System.out.println(this.getClass().getSimpleName() + " running on port " + server.getLocalPort());
        this.clients = new ArrayList();
        this.balance = 1000.0;
        this.log = new StringBuilder();
        this.caller.setBalance(balance);
    }
    
    @Override
    public void run() {
        Socket socket = null;
        while(true) {
            try {
                socket = this.server.accept();
                PrintWriter output = new PrintWriter(socket.getOutputStream());
                newClient(output);
                (new ServerHandler(socket, output, this)).start();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public synchronized void newClient(PrintWriter output) throws IOException {
        clients.add(output);
    }
    
    public synchronized void removeClient(PrintWriter output) {
        clients.remove(output);
        output.close();
    }
    
    public List getClients() {
        return clients;
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.server.close();
    }
    
    public String getLog() {
        return this.log.toString();
    }

    void clearLog() {
        this.log = new StringBuilder();
    }
    
}
