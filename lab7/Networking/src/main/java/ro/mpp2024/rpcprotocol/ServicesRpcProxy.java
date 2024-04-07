package ro.mpp2024.rpcprotocol;



import ro.mpp2024.*;
import ro.mpp2024.Exception;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


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

    }

    private boolean isUpdate(Response response){
        return response.type()== ResponseType.FRIEND_LOGGED_OUT || response.type()== ResponseType.FRIEND_LOGGED_IN;
    }

    @Override
    public boolean handleLogin(String username, String password, IObserver client) throws Exception {
        return false;
    }

    @Override
    public void logout(Agentie user, IObserver client) throws Exception {

    }

    @Override
    public Iterable<Excursie> getAllExcursii() {
        return null;
    }

    @Override
    public Iterable<Excursie> getExcursiiBetweenHours(String obiectiv, LocalTime ora1, LocalTime ora2) {
        return null;
    }

    @Override
    public Integer getFreeSeats(int id) throws SQLException {
        return null;
    }

    @Override
    public void addRezervare(Excursie ex, String nume, String telefon, int bilet) {

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
