package ro.mpp2024.proto;

import java.net.Socket;

public abstract class ConcurrentServerProto extends AbstractServerProto {

    public ConcurrentServerProto(String host, int port) {
        super(host, port);
    }

    @Override
    public void processRequest(Socket client) {
        Thread t = createWorker(client);
        t.start();
    }

    protected abstract Thread createWorker(Socket client);
}
