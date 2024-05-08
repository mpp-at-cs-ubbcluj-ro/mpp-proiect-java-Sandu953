package ro.mpp2024.proto;

import Org.Example.ProtocolProto;
import ro.mpp2024.*;
import ro.mpp2024.Exception;

import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class ProjectProtoWorker implements IObserver {
    private IServices server;
    private Socket connection;

    private InputStream inputStream;
    private OutputStream outputStream;
    private volatile boolean connected;

    public ProjectProtoWorker(IServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            inputStream = connection.getInputStream();
            outputStream = connection.getOutputStream();
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (connected) {
            try {
                ProtocolProto.Request request = ProtocolProto.Request.parseDelimitedFrom(inputStream);
                ProtocolProto.Response response = handleRequest(request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            inputStream.close();
            outputStream.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendResponse(ProtocolProto.Response response) {
        System.out.println("Sending response ...");
        try {
            response.writeDelimitedTo(outputStream);
            outputStream.flush();
            System.out.println("Response sent ...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ProtocolProto.Response handleRequest(ProtocolProto.Request request) {
        ProtocolProto.Response response = null;
        if (request.getType() == ProtocolProto.Request.RequestType.LOGIN) {
            System.out.println("Login request ...");
            try {
                synchronized (server) {
                    if (!server.handleLogin(request.getAgentie().getUsername(), request.getAgentie().getPass(), this))
                        throw new Exception("Nume sau parola gresite!");
                }
                System.out.println("Login OK");
                return ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.OK).build();
            } catch (Exception e) {
                connected = false;
                return ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.ERROR).setError(e.getMessage()).build();
            }
        }

        if (request.getType() == ProtocolProto.Request.RequestType.LOGOUT) {
            System.out.println("Logout request...");
            try {
                synchronized (server) {
                    Agentie ag = new Agentie(request.getAgentie().getUsername());
                    ag.setId(request.getAgentie().getId());
                    server.logout(ag, this);
                }
                connected = false;
                return ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.OK).build();
            } catch (Exception e) {
                return ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.ERROR).setError(e.getMessage()).build();
            }
        }

        if (request.getType() == ProtocolProto.Request.RequestType.GET_EXCURSII) {
            System.out.println("All excursii request");
            try {
                synchronized (server) {
                    Iterable<Excursie> excursii = server.getAllExcursii();
                    ProtocolProto.Response.Builder builder = ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.EXCURSII);
                    for (Excursie excursie : excursii) {


                        builder.addExcursii(ProtocolProto.Excursie.newBuilder()
                                .setId(excursie.getId())
                                .setObiectivTuristic(excursie.getObiectivTuristic())
                                .setNumeTransport(excursie.getNumeTransport())
                                .setOraPlecare(excursie.getOraPlecare().toString())
                                .setPret(excursie.getPret())
                                .setNrLocuri(excursie.getNrLocuri())
                                .setLocuriLibere(excursie.getLocuriLibere())
                                .build());
                    }
                    return builder.build();
                }
            } catch (Exception e) {
                return ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.ERROR).setError(e.getMessage()).build();
            }
        }
        if (request.getType() == ProtocolProto.Request.RequestType.GET_EXCURSII_ORE) {
            try {
                synchronized (server) {
                    String[] parts = request.getExcursie().getOraPlecare().split("___");
                    String ora1 = parts[0];
                    String ora2 = parts[1];

                    System.out.println(ora1);
                    System.out.println(ora2);

                    String timeString1 =  ora1; // Example time string
                    String timeString2 =  ora2; // Example time string
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                    LocalTime localTime1 = LocalTime.parse(timeString1, formatter);
                    LocalTime localTime2 = LocalTime.parse(timeString2, formatter);

                    Iterable<Excursie> excursii = server.getExcursiiBetweenHours(request.getExcursie().getObiectivTuristic(), localTime1, localTime2);
                    ProtocolProto.Response.Builder builder = ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.EXCURSII_ORE);
                    for (Excursie excursie : excursii) {
                        builder.addExcursii(ProtocolProto.Excursie.newBuilder()
                                .setId(excursie.getId())
                                .setObiectivTuristic(excursie.getObiectivTuristic())
                                .setNumeTransport(excursie.getNumeTransport())
                                .setOraPlecare(excursie.getOraPlecare().toString())
                                .setPret(excursie.getPret())
                                .setNrLocuri(excursie.getNrLocuri())
                                .setLocuriLibere(excursie.getLocuriLibere())
                                .build());
                    }
                    return builder.build();
                }
            } catch (Exception e) {
                return ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.ERROR).setError(e.getMessage()).build();
            }
        }

        if (request.getType() == ProtocolProto.Request.RequestType.GET_FREE_SEATS) {
            try {
                synchronized (server) {
                    int nr = server.getFreeSeats((int) request.getExcursie().getId());
                    return ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.OK).setExcursie(
                            ProtocolProto.Excursie.newBuilder().setLocuriLibere(nr).build()
                    ).build();
                }
            } catch (Exception | SQLException e) {
                return ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.ERROR).setError(e.getMessage()).build();
            }
        }

        if (request.getType() == ProtocolProto.Request.RequestType.GET_ID) {
            try {
                synchronized (server) {
                    long id = server.getId(request.getAgentie().getUsername(), request.getAgentie().getPass());
                    return ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.OK).setAgentie(
                            ProtocolProto.Agentie.newBuilder().setId(id).build()
                    ).build();
                }
            } catch (Exception e) {
                return ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.ERROR).setError(e.getMessage()).build();
            }
        }

        if (request.getType() == ProtocolProto.Request.RequestType.UPDATE_SEARCH) {
            // Implement this method if needed
            return null;
        }

        if (request.getType() == ProtocolProto.Request.RequestType.ADD_REZ) {
            try {
                synchronized (server) {
                    server.addRezervare(request.getRezervare().getId(), request.getRezervare().getExcursie(),
                            request.getRezervare().getNumeClient(), request.getRezervare().getNrTelefon(),
                            request.getRezervare().getNrLocuri());
                    return ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.OK).build();
                }
            } catch (Exception e) {
                return ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.ERROR).setError(e.getMessage()).build();
            }
        }

        // Implement other request types similarly

        return response;
    }


    @Override
    public void rezervationMade() throws Exception {
        //try {
            ProtocolProto.Response response = ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.ADDED_REZERVATION).build();
            sendResponse(response);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}