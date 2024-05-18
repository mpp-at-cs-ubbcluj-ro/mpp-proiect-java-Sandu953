package ro.mpp2024;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServicesImpl implements IServices {

    private AgentieORMRepo agentieRepo;
    private ExcursieRepo excursieRepo;
    private RezervareRepo rezervareRepo;
    private Map<Long, IObserver> loggedClients;

    public ServicesImpl(AgentieORMRepo agentieRepo, ExcursieRepo excursieRepo, RezervareRepo rezervareRepo) {
        this.agentieRepo = agentieRepo;
        this.excursieRepo = excursieRepo;
        this.rezervareRepo = rezervareRepo;
        loggedClients = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized boolean handleLogin(String username, String password, IObserver client) throws Exception {
        Agentie agentie;
        agentie = agentieRepo.findBy(username, password);
        if (agentie != null) {
            System.out.println(agentie.getId());
            if (loggedClients.get(agentie.getId()) != null)
                return false;
            System.out.println(agentie.getId());
            loggedClients.put(agentie.getId(), client);

        } else
            return false;
        return agentieRepo.loginByUsernamePassword(username, password);
    }


    public long getId(String username, String password) {
        Agentie ag = agentieRepo.findBy(username, password);
        return ag.getId();
    }

    @Override
    public Iterable<Excursie> getExcursiiBetweenHours(String ora1, String ora2, String obiectiv) {
        return excursieRepo.findExcursieBetweenHoursString(ora1, ora2, obiectiv);
    }

    public synchronized void logout(Agentie user, IObserver client) throws Exception {
        Agentie ag = agentieRepo.findByUser(user.getUsername());
        if(ag == null)
            throw new Exception("User " + user.getId() + " is not logged in.");
        IObserver localClient = loggedClients.remove(ag.getId());
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
    public synchronized void addRezervare(long id,long ex, String nume, String telefon, int bilet) {
        try{
            Rezervare rez = new Rezervare(ex, nume, telefon, bilet);
            rezervareRepo.save(rez);
            System.out.println("Notifying");
            notifyRezervare(id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private final int defaultThreadsNo = 5;

    private void notifyRezervare(long id) throws Exception {
//        Iterable<Agentie> agenties = agentieRepo.findAll();
//        System.out.println("Logged " + agenties);

        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        for (Map.Entry<Long, IObserver> entry : loggedClients.entrySet()) {
            System.out.println("AAA"+ entry.getKey());
            IObserver chatClient = loggedClients.get(entry.getKey());
            if (chatClient != null && entry.getKey() != id)
                executor.execute(() -> {
                    try {
                        //System.out.println("Notifying [" + ag.getId()+ "] friend ["+user.getId()+"] logged in.");
                        chatClient.rezervationMade();
                    } catch (Exception e) {
                        System.err.println("Error notifying " + e);
                    }
                });
        }
        executor.shutdown();
    }
}