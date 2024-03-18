package ro.mpp2024.repository;

import ro.mpp2024.domain.Agentie;
import ro.mpp2024.domain.Excursie;

public interface IAgentieRepo extends IRepository<Integer, Agentie>{
    Iterable<Agentie> findByUsername(String username);
}
