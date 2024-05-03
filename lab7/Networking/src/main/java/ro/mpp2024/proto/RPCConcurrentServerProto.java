package ro.mpp2024.proto;

import ro.mpp2024.IServices;

import java.net.Socket;

public class RPCConcurrentServerProto extends ConcurrentServerProto {
    private IServices server;
    private ProjectProtoWorker worker;

    public RPCConcurrentServerProto(String host, int port, IServices server) {
        super(host, port);
        this.server = server;
        System.out.println("RPCConcurrentServer...");
    }

    @Override
    protected Thread createWorker(Socket client) {
        worker = new ProjectProtoWorker(server, client);
        return new Thread(worker::run);
    }
}
