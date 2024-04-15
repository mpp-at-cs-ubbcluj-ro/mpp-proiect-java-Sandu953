package ro.mpp2024.rpcprotocol;



import ro.mpp2024.*;
import ro.mpp2024.Exception;
import ro.mpp2024.dto.AgentieDTO;
import ro.mpp2024.dto.CautareDTO;
import ro.mpp2024.dto.DTOUtils;
import ro.mpp2024.dto.RezervareDTO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static ro.mpp2024.dto.DTOUtils.getDTO;


public class ServicesRpcProxy implements IServices {
    private String host;
    private int port;

    private IObserver client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;
    public ServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses=new LinkedBlockingQueue<Response>();
    }

//    public void login(User user, IChatObserver client) throws ChatException {
//        initializeConnection();
//        UserDTO udto= DTOUtils.getDTO(user);
//        Request req=new Request.Builder().type(RequestType.LOGIN).data(udto).build();
//        sendRequest(req);
//        Response response=readResponse();
//        if (response.type()== ResponseType.OK){
//            this.client=client;
//            return;
//        }
//        if (response.type()== ResponseType.ERROR){
//            String err=response.data().toString();
//            closeConnection();
//            throw new ChatException(err);
//        }
//    }
//

//    public void logout(User user, IChatObserver client) throws ChatException {
//        UserDTO udto= DTOUtils.getDTO(user);
//        Request req=new Request.Builder().type(RequestType.LOGOUT).data(udto).build();
//        sendRequest(req);
//        Response response=readResponse();
//        closeConnection();
//        if (response.type()== ResponseType.ERROR){
//            String err=response.data().toString();
//            throw new ChatException(err);
//        }
//    }





    private void closeConnection() {
        finished=true;
        try {
            input.close();
            output.close();
            connection.close();
            client=null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendRequest(Request request)throws Exception {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new Exception("Error sending object "+e);
        }

    }

    private Response readResponse() throws Exception {
        Response response=null;
        try{
            response=qresponses.take();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }
    private void initializeConnection() throws Exception {
        try {
            connection=new Socket(host,port);
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            finished=false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void startReader(){
        Thread tw=new Thread(new ReaderThread());
        tw.start();
    }


//    private void handleUpdate(Response response){
//        if (response.type()== ResponseType.FRIEND_LOGGED_IN){
//
//            User friend= DTOUtils.getFromDTO((UserDTO) response.data());
//            System.out.println("Friend logged in "+friend);
//            try {
//                client.friendLoggedIn(friend);
//            } catch (ChatException e) {
//                e.printStackTrace();
//            }
//        }
//        if (response.type()== ResponseType.FRIEND_LOGGED_OUT){
//            User friend= DTOUtils.getFromDTO((UserDTO)response.data());
//            System.out.println("Friend logged out "+friend);
//            try {
//                client.friendLoggedOut(friend);
//            } catch (ChatException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (response.type()== ResponseType.NEW_MESSAGE){
//            Message message= DTOUtils.getFromDTO((MessageDTO)response.data());
//            try {
//                client.messageReceived(message);
//            } catch (ChatException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private void handleUpdate(Response response){
        if(response.type() == ResponseType.UPDATE_SHEARCH || response.type() == ResponseType.ADDED_REZERVATION){
            try{
                client.rezervationMade();
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    private boolean isUpdate(Response response){
        return response.type()== ResponseType.UPDATE_SHEARCH || response.type()== ResponseType.ADDED_REZERVATION;

    }

    @Override
    public boolean handleLogin(String username, String password, IObserver client) throws Exception {
        initializeConnection();
//        Agentie ag = new Agentie(username);
//        AgentieDTO agentieDTO= getDTO(ag);
        AgentieDTO agentieDTO = new AgentieDTO(username, password);
        Request req=new Request.Builder().type(RequestType.LOGIN).data(agentieDTO).build();
        sendRequest(req);
        Response response=readResponse();

        if (response.type()== ResponseType.OK){
            this.client=client;
            return true;
        }
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            closeConnection();
            return false;
            //throw new Exception(err);
        }
        return false;
    }

    @Override
    public void logout(Agentie user, IObserver client) throws Exception {
        AgentieDTO udto= getDTO(user);
        Request req=new Request.Builder().type(RequestType.LOGOUT).data(udto).build();
        sendRequest(req);
        Response response=readResponse();
        closeConnection();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new Exception(err);
        }
    }

    @Override
    public Iterable<Excursie> getAllExcursii() throws Exception{
        Request req=new Request.Builder().type(RequestType.GET_EXCURSII).build();
        sendRequest(req);
        Response response=readResponse();
        return (Iterable<Excursie>) response.data();
    }

    @Override
    public Iterable<Excursie> getExcursiiBetweenHours(String obiectiv, LocalTime ora1, LocalTime ora2) throws Exception {
        CautareDTO cautareDTO = new CautareDTO(obiectiv, ora1, ora2);
        Request req=new Request.Builder().type(RequestType.GET_EXCURSII_ORE).data(cautareDTO).build();
        sendRequest(req);
        Response response=readResponse();
        return (Iterable<Excursie>) response.data();
    }

    @Override
    public Integer getFreeSeats(int id) throws SQLException , Exception{
        Request req=new Request.Builder().type(RequestType.GET_FREE_SEATS).data(id).build();
        sendRequest(req);
        Response response=readResponse();
        return (int) response.data();
    }

    @Override
    public void addRezervare(long id,long ex, String nume, String telefon, int bilet) throws Exception {
        Rezervare rez = new Rezervare(ex, nume, telefon, bilet);
        RezervareDTO rdto = DTOUtils.getDTO(rez);
        rdto.setId(id);
        System.out.println("ADD_REZ request");
        Request request = new Request.Builder().type(RequestType.ADD_REZ).data(rdto).build();
        sendRequest(request);
        Response response = readResponse();
        System.out.println(response.type());
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            System.out.println("Aici se inchide"+ err);
            closeConnection();
            throw new Exception(err);
        }
    }

    @Override
    public long getId(String username, String password) throws Exception {
        AgentieDTO agentieDTO = new AgentieDTO(username, password);
        Request request = new Request.Builder().type(RequestType.GET_ID).data(agentieDTO).build();
        sendRequest(request);
        Response response = readResponse();
        return (long) response.data();
    }

    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    Object response=input.readObject();
                    System.out.println("response received "+response);
                    if (isUpdate((Response)response)){
                        handleUpdate((Response)response);
                    }else{

                        try {
                            qresponses.put((Response)response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Reading error "+e);
                } catch (ClassNotFoundException e) {
                    System.out.println("Reading error "+e);
                }
            }
        }
    }
}
