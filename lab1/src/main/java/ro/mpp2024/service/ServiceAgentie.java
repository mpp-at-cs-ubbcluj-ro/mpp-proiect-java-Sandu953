package ro.mpp2024.service;

import ro.mpp2024.domain.Agentie;
import ro.mpp2024.repository.AgentieRepo;

public class ServiceAgentie {
    private AgentieRepo agentieRepo;

    public ServiceAgentie(AgentieRepo agentieRepo) {
        this.agentieRepo = agentieRepo;
    }

    public void saveAgentie(String username, String password) {
        agentieRepo.save(username, password);
    }

    public Agentie deleteAgentie(Integer id) {
        Agentie agentie = agentieRepo.findOne(id);
        agentieRepo.delete(id);
        return agentie;
    }

    public Agentie getAgentie(int id) {
        return agentieRepo.findOne(id);
    }

    public Iterable<Agentie> getAllAgentii() {
        return agentieRepo.findAll();
    }

    public void updateAgentie(Integer id, String username, String password) {
        Agentie agentie = new Agentie(username);
        agentie.setId(id.longValue());
        agentieRepo.update(id, agentie);
    }

    public boolean handleLogin(String username, String password) {
        return agentieRepo.loginByUsernamePassword(username, password);
    }

}
