package ro.mpp2024.proto;

import Org.Example.ProtocolProto;
import ro.mpp2024.*;
import ro.mpp2024.Exception;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProjectServerProxy implements IServices {
    private String host;
    private int port;
    private IObserver client;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Socket socket;
    private Queue<ProtocolProto.Response> responses;
    private volatile boolean finished;
    private final Lock lock = new ReentrantLock();
    private final Condition responseAvailable = lock.newCondition();

    public ProjectServerProxy(String host, int port) {
        this.host = host;
        this.port = port;
        responses = new LinkedList<>();
    }

    private void closeConnection() {
        finished = true;
        try {
            outputStream.close();
            inputStream.close();
            socket.close();
            client = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(ProtocolProto.Request request) {
        lock.lock();
        try {
            //outputStream.writeObject(request);
            request.writeDelimitedTo(outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private ProtocolProto.Response readResponse() {
        lock.lock();
        try {
            while (responses.isEmpty()) {
                responseAvailable.await();
            }
            return responses.poll();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } finally {
            lock.unlock();
        }
//        ProtocolProto.Response response=null;
//        try{
//            response = responses.take();
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return response;
    }

    private void initializeConnection() {
        try {
            socket = new Socket(host, port);
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
            finished = false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReader() {
        new Thread(this::run).start();
    }

    public void run() {
        while (!finished) {
            try {
                ProtocolProto.Response response = ProtocolProto.Response.parseDelimitedFrom(inputStream);

                if (response.getType() != ProtocolProto.Response.ResponseType.ADDED_REZERVATION) {
                    lock.lock();
                    responses.offer(response);
                    responseAvailable.signal();
                    lock.unlock();
                } else {
                    try {
                        client.rezervationMade();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                System.out.println("Reading error " + e);
                finished = true;
            }
        }
    }

    @Override
    public boolean handleLogin(String username, String password, IObserver client) {
        initializeConnection();
        ProtocolProto.Request req = ProtocolProto.Request.newBuilder()
                .setType(ProtocolProto.Request.RequestType.LOGIN)
                .setAgentie(ProtocolProto.Agentie.newBuilder()
                        .setId(1)
                        .setUsername(username)
                        .setPass(password)
                        .build())
                .build();
        sendRequest(req);
        ProtocolProto.Response response = readResponse();
        if (response.getType() == ProtocolProto.Response.ResponseType.OK) {
            this.client = client;
            return true;
        }
        if (response.getType() == ProtocolProto.Response.ResponseType.ERROR) {
            closeConnection();
            return false;
        }
        return false;
    }


    @Override
    public void logout(Agentie user, IObserver client) {
        System.out.println("Logout request...");
        ProtocolProto.Request req = ProtocolProto.Request.newBuilder()
                .setType(ProtocolProto.Request.RequestType.LOGOUT)
                .setAgentie(ProtocolProto.Agentie.newBuilder()
                        .setId(1)
                        .setUsername(user.getUsername())
                        .setPass("")
                        .build())
                .build();
        sendRequest(req);
        ProtocolProto.Response response = readResponse();
        closeConnection();
        if (response.getType() == ProtocolProto.Response.ResponseType.ERROR) {
            throw new RuntimeException("Error logging out" + response.getError());
        }
    }

    @Override
    public List<Excursie> getAllExcursii() {
        System.out.println("Get all excursii request ...");
        ProtocolProto.Request req = ProtocolProto.Request.newBuilder()
                .setType(ProtocolProto.Request.RequestType.GET_EXCURSII)
                .build();
        sendRequest(req);
        ProtocolProto.Response response = readResponse();
        List<Excursie> excursii = new ArrayList<>();
        for (ProtocolProto.Excursie excursie : response.getExcursiiList()) {
            String timeString = excursie.getOraPlecare(); // Example time string
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalTime localTime = LocalTime.parse(timeString, formatter);

            excursii.add(new Excursie(
                    excursie.getObiectivTuristic(),
                    excursie.getNumeTransport(),
                    localTime,
                    excursie.getPret(),
                    excursie.getNrLocuri(),
                    excursie.getLocuriLibere()));
        }
        return excursii;
    }

    @Override
    public Iterable<Excursie> getExcursiiBetweenHours(String obiectiv, LocalTime ora1, LocalTime ora2) throws Exception {
        String oreP = ora1.toString() + "___" + ora2.toString();
        System.out.println("1234");
        System.out.println(oreP + " " + obiectiv);
        System.out.println("Get excursii ora request ...");

        ProtocolProto.Request req = ProtocolProto.Request.newBuilder()
                .setType(ProtocolProto.Request.RequestType.GET_EXCURSII_ORE)
                .setExcursie(ProtocolProto.Excursie.newBuilder()
                        .setObiectivTuristic(obiectiv)
                        .setOraPlecare(oreP)
                        .build())
                .build();
        sendRequest(req);
        ProtocolProto.Response response = readResponse();
        List<Excursie> excursii = new ArrayList<>();
        for (ProtocolProto.Excursie excursie : response.getExcursiiList()) {
            String timeString = excursie.getOraPlecare(); // Example time string
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalTime localTime = LocalTime.parse(timeString, formatter);
            excursii.add(new Excursie(
                    excursie.getObiectivTuristic(),
                    excursie.getNumeTransport(),
                    localTime,
                    excursie.getPret(),
                    excursie.getNrLocuri(),
                    excursie.getLocuriLibere()));
        }
        return excursii;
    }

//    @Override
//    public Iterable<Excursie> getExcursiiBetweenHours(String obiectiv, String ora1, String ora2) {
//
//    }

    @Override
    public Integer getFreeSeats(int id) {
        System.out.println("Get free seats request ...");
        ProtocolProto.Request req = ProtocolProto.Request.newBuilder()
                .setType(ProtocolProto.Request.RequestType.GET_FREE_SEATS)
                .setExcursie(ProtocolProto.Excursie.newBuilder()
                        .setId(id)
                        .build())
                .build();
        sendRequest(req);
        ProtocolProto.Response response = readResponse();
        return response.getExcursie().getLocuriLibere();
    }

    @Override
    public void addRezervare(long id, long ex, String nume, String telefon, int nr) {
        System.out.println("Add rezervare request ...");
        ProtocolProto.Request req = ProtocolProto.Request.newBuilder()
                .setType(ProtocolProto.Request.RequestType.ADD_REZ)
                .setRezervare(ProtocolProto.Rezervare.newBuilder()
                        .setId(id)
                        .setExcursie(ex)
                        .setNumeClient(nume)
                        .setNrTelefon(telefon)
                        .setNrLocuri(nr)
                        .build())
                .build();
        sendRequest(req);
        readResponse();
    }

    @Override
    public long getId(String username, String password) {
        System.out.println("Get id request ...");
        ProtocolProto.Request req = ProtocolProto.Request.newBuilder()
                .setType(ProtocolProto.Request.RequestType.GET_ID)
                .setAgentie(ProtocolProto.Agentie.newBuilder()
                        .setId(1)
                        .setUsername(username)
                        .setPass(password)
                        .build())
                .build();
        sendRequest(req);
        ProtocolProto.Response response = readResponse();
        return response.getAgentie().getId();
    }

    @Override
    public Iterable<Excursie> getExcursiiBetweenHours( String obiectiv,String ora1, String ora2) {
        String oreP = ora1 + "___" + ora2;
        System.out.println("Get excursii ora request ...");
        System.out.println(oreP + " " + obiectiv);
        ProtocolProto.Request req = ProtocolProto.Request.newBuilder()
                .setType(ProtocolProto.Request.RequestType.GET_EXCURSII_ORE)
                .setExcursie(ProtocolProto.Excursie.newBuilder()
                        .setObiectivTuristic(obiectiv)
                        .setOraPlecare(oreP)
                        .build())
                .build();
        sendRequest(req);
        ProtocolProto.Response response = readResponse();
        List<Excursie> excursii = new ArrayList<>();
        for (ProtocolProto.Excursie excursie : response.getExcursiiList()) {

            String timeString = excursie.getOraPlecare(); // Example time string
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalTime localTime = LocalTime.parse(timeString, formatter);

            Excursie ex = new Excursie(
                    excursie.getObiectivTuristic(),
                    excursie.getNumeTransport(),
                    localTime,
                    excursie.getPret(),
                    excursie.getNrLocuri(),
                    excursie.getLocuriLibere());
            ex.setId(excursie.getId());
            excursii.add(ex);
        }
        return excursii;
    }
}