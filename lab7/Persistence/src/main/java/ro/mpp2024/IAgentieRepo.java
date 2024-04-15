package ro.mpp2024;

import ro.mpp2024.Agentie;

public interface IAgentieRepo extends IRepository<Integer, Agentie> {
    boolean loginByUsernamePassword(String username, String password);

    void save(String username, String password);
}
