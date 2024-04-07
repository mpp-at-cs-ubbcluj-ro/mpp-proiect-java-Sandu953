package ro.mpp2024.rpcprotocol;


import ro.mpp2024.Exception;
import ro.mpp2024.IObserver;
import ro.mpp2024.IServices;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;


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

//    private Response handleLOGIN(Request request){
//        System.out.println("Login request ..."+request.type());
//        UserDTO udto=(UserDTO)request.data();
//        User user=DTOUtils.getFromDTO(udto);
//        try {
//            server.login(user, this);
//            return okResponse;
//        } catch (ChatException e) {
//            connected=false;
//            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
//        }
//    }

//    private Response handleLOGOUT(Request request){
//        System.out.println("Logout request...");
//        UserDTO udto=(UserDTO)request.data();
//        User user=DTOUtils.getFromDTO(udto);
//        try {
//            server.logout(user, this);
//            connected=false;
//            return okResponse;
//
//        } catch (ChatException e) {
//            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
//        }
//    }

    private void sendResponse(Response response) throws IOException{
        System.out.println("sending response "+response);
        synchronized (output) {
            output.writeObject(response);
            output.flush();
        }
    }

    @Override
    public void rezervationMade() throws Exception {

    }
}
