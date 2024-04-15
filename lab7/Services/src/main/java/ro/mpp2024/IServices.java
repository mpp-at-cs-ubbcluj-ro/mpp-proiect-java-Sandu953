package ro.mpp2024;

import java.sql.SQLException;
import java.time.LocalTime;

public interface IServices {
    boolean handleLogin(String username, String password, IObserver client) throws Exception;
    void logout(Agentie user, IObserver client) throws Exception;
    Iterable<Excursie> getAllExcursii() throws Exception;
    Iterable<Excursie> getExcursiiBetweenHours(String obiectiv, LocalTime ora1, LocalTime ora2) throws Exception;
    Integer getFreeSeats(int id) throws SQLException, Exception ;
    void addRezervare(long id, long ex, String nume, String telefon, int bilet) throws Exception;
    long getId(String username, String password) throws Exception;
}
