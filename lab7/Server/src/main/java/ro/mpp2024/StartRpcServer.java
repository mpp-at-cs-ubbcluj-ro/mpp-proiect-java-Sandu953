package ro.mpp2024;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ro.mpp2024.proto.ConcurrentServerProto;
import ro.mpp2024.proto.RPCConcurrentServerProto;
import ro.mpp2024.utils.ServerException;
import ro.mpp2024.utils.AbstractServer;
import java.io.IOException;
import java.util.Properties;
import ro.mpp2024.utils.RpcConcurrentServer;

public class StartRpcServer {
    private static int defaultPort=55555;
    private static SessionFactory sessionFactory;


    private static void setUp() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        sessionFactory = new MetadataSources(registry)
                .buildMetadata()
                .buildSessionFactory();
    }

    private static void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
    public static void main(String[] args) {
        // UserRepository agentieRepo=new UserRepositoryMock();
        Properties serverProps=new Properties();
        try {
            serverProps.load(StartRpcServer.class.getResourceAsStream("/chatserver.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find chatserver.properties "+e);
            return;
        }

        setUp();

        //AgentieRepo agentieRepo=new AgentieRepo(serverProps);
        AgentieORMRepo agentieRepo=new AgentieORMRepo(sessionFactory);
        ExcursieRepo excursieRepo=new ExcursieRepo(serverProps);
        RezervareRepo rezervareRepo=new RezervareRepo(serverProps, excursieRepo);
        IServices serverImpl=new ServicesImpl(agentieRepo, excursieRepo, rezervareRepo);
        int ServerPort=defaultPort;
        try {
            ServerPort = Integer.parseInt(serverProps.getProperty("chat.server.port"));
        }catch (NumberFormatException nef){
            System.err.println("Wrong  Port Number"+nef.getMessage());
            System.err.println("Using default port "+defaultPort);
        }
        System.out.println("Starting server on port: "+55555);

        //AbstractServer server = new RpcConcurrentServer(ServerPort, serverImpl);
        //AbstractServer server = new RpcConcurrentServer(ServerPort, serverImpl);
        RPCConcurrentServerProto server = new RPCConcurrentServerProto("127.0.0.1",55555, serverImpl);
        //server.start();
        try {
            server.start();
        } finally {
           tearDown();
        }
    }
}
