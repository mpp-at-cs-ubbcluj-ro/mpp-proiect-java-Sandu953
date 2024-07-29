package ro.mpp2024.persistance;


import ro.mpp2024.model.Agentie;

public interface IAgentieRepo extends IRepository<Integer, Agentie> {
    boolean loginByUsernamePassword(String username, String password);

    void save(String username, String password);
}
