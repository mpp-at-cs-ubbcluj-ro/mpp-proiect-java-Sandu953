package ro.mpp2024.proto;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class AbstractServerProto {
    private ServerSocket server;
    private String host;
    private int port;

    public AbstractServerProto(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        try {
            server = new ServerSocket(port, 50, InetAddress.getByName(host));
            while (true) {
                System.out.println("Waiting for clients ...");
                Socket client = server.accept();
                System.out.println("Client connected ...");
                processRequest(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void processRequest(Socket client);
}
