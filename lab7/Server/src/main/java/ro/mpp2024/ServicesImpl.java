package ro.mpp2024;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServicesImpl implements IServices {

    private AgentieRepo agentieRepo;
    private ExcursieRepo excursieRepo;
    private RezervareRepo rezervareRepo;
    private Map<Long, IObserver> loggedClients;

    public ServicesImpl(AgentieRepo agentieRepo, ExcursieRepo excursieRepo, RezervareRepo rezervareRepo) {
        this.agentieRepo = agentieRepo;
        this.excursieRepo = excursieRepo;
        this.rezervareRepo = rezervareRepo;
        loggedClients = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized boolean handleLogin(String username, String password, IObserver client) throws Exception {
        Agentie agentie;
        agentie = AgentieRepo.findBy(username, password);
        if (agentie != null) {
            if (loggedClients.get(agentie.getId()) != null)
                return false;
            loggedClients.put(agentie.getId(), client);

        } else
            return false;
        return agentieRepo.loginByUsernamePassword(username, password);
    }

    public synchronized void logout(Agentie user, IObserver client) throws Exception {
        IObserver localClient = loggedClients.remove(user.getId());
        if (localClient == null)
            throw new Exception("User " + user.getId() + " is not logged in.");
    }


    @Override
    public synchronized Iterable<ro.mpp2024.Excursie> getAllExcursii() {
        return excursieRepo.findAll();
    }

    @Override
    public synchronized Iterable<ro.mpp2024.Excursie> getExcursiiBetweenHours(String obiectiv, LocalTime ora1, LocalTime ora2) {
        return excursieRepo.findExcursieBetweenHours(ora1, ora2, obiectiv);
    }

    @Override
    public synchronized Integer getFreeSeats(int id) throws SQLException {
        return excursieRepo.findLocuriLibere(id);
    }

    @Override
    public synchronized void addRezervare(Excursie ex, String nume, String telefon, int bilet) {
        try{
            Rezervare rez = new Rezervare(ex.id, nume, telefon, bilet);
            rezervareRepo.save(rez);
            notifyRezervare();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private final int defaultThreadsNo = 5;

    private void notifyRezervare() throws Exception {
        Iterable<Agentie> agenties = agentieRepo.findAll();
        System.out.println("Logged " + agenties);

        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        for (Agentie ag : agenties) {
            IObserver chatClient = loggedClients.get(ag.getId());
            if (chatClient != null)
                executor.execute(() -> {
                    try {
                        //System.out.println("Notifying [" + ag.getId()+ "] friend ["+user.getId()+"] logged in.");
                        chatClient.rezervationMade();
                    } catch (Exception e) {
                        System.err.println("Error notifying " + e);
                    }
                });
        }
    }
}