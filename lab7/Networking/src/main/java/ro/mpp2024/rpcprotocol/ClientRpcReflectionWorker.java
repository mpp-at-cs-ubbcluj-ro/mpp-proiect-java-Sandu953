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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalTime;


public class ClientRpcReflectionWorker implements Runnable, IObserver {
    private IServices server;
    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;
    public ClientRpcReflectionWorker(IServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try{
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            connected=true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while(connected){
            try {
                //System.out.println(input.readObject());
                Object request=input.readObject();
                Response response=handleRequest((Request)request);
                if (response!=null){
                    sendResponse(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error "+e);
        }
    }


//    public void friendLoggedIn(User friend) throws ChatException {
//        UserDTO udto=DTOUtils.getDTO(friend);
//        Response resp=new Response.Builder().type(ResponseType.FRIEND_LOGGED_IN).data(udto).build();
//        System.out.println("Friend logged in "+friend);
//        try {
//            sendResponse(resp);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    public void friendLoggedOut(User friend) throws ChatException {
//        UserDTO udto=DTOUtils.getDTO(friend);
//        Response resp=new Response.Builder().type(ResponseType.FRIEND_LOGGED_OUT).data(udto).build();
//        System.out.println("Friend logged out "+friend);
//        try {
//            sendResponse(resp);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private static Response okResponse=new Response.Builder().type(ResponseType.OK).build();

    private Response handleRequest(Request request){
        Response response=null;
        String handlerName="handle"+(request).type();
        System.out.println("HandlerName "+handlerName);
        try {
            Method method=this.getClass().getDeclaredMethod(handlerName, Request.class);
            response=(Response)method.invoke(this,request);
            System.out.println("Method "+handlerName+ " invoked");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return response;
    }

    private Response handleLOGIN(Request request) {
        System.out.println("Login request ..." + request.type());
        AgentieDTO udto = (AgentieDTO) request.data();
        try {
            if(!server.handleLogin(udto.getUser(), udto.getPass(), this)) throw new Exception("Wrong username or password");
            return okResponse;
        } catch (Exception e) {
            connected = false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }


    public void handleGET_EXCURSII(Request request) throws Exception {
        Response resp = new Response.Builder().type(ResponseType.EXCURSII).data(server.getAllExcursii()).build();
        try {
            sendResponse(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleGET_FREE_SEATS(Request request) throws Exception, SQLException {
        int id = (int) request.data();
        Response resp = new Response.Builder().type(ResponseType.OK).data(server.getFreeSeats(id)).build();
        try {
            sendResponse(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleGET_EXCURSII_ORE(Request request) throws Exception {
        CautareDTO cautareDTO = (CautareDTO) request.data();
        LocalTime ora1 = cautareDTO.getOra1();
        LocalTime ora2 = cautareDTO.getOra2();
        String obiectiv = cautareDTO.getObiectiv();
        Response resp = new Response.Builder().type(ResponseType.EXCURSII_ORE).data(server.getExcursiiBetweenHours(obiectiv,ora1, ora2)).build();
        try {
            sendResponse(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Response handleLOGOUT(Request request){
        System.out.println("Logout request...");
        AgentieDTO udto=(AgentieDTO) request.data();
        Agentie user= DTOUtils.getFromDTO(udto);
        System.out.println(user.getId());
        try {

            server.logout(user, this);
            connected=false;
            return okResponse;

        } catch (Exception e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private void sendResponse(Response response) throws IOException{
        System.out.println("sending response "+response);
        synchronized (output) {
            output.writeObject(response);
            output.flush();
        }
    }

    public void handleADD_REZ(Request request) throws Exception {
        RezervareDTO rdto = (RezervareDTO) request.data();
        Rezervare rezervare = DTOUtils.getFromDTO(rdto);
        System.out.println("ADD_REZ handle response!!!");
        server.addRezervare(rdto.getId(), rezervare.getExcursie(), rezervare.getNumeClient(), rezervare.getNrTelefon(), rezervare.getNrLocuri());
        System.out.println("ADD_REZ h 222 response!!!");
        Response resp = new Response.Builder().type(ResponseType.ADDED_REZERVATION).build();
        try {
            sendResponse(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleGET_ID(Request request) throws Exception {
//        System.out.println("UPDATE response!!!");
        AgentieDTO udto = (AgentieDTO) request.data();
        Response resp = new Response.Builder().type(ResponseType.OK).data(server.getId(udto.getUser(), udto.getPass())).build();
        try {
            sendResponse(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void rezervationMade() throws Exception {
        System.out.println("ADD_REZ response!!!");
        Response resp=new Response.Builder().type(ResponseType.ADDED_REZERVATION).build();
        try {
            sendResponse(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
