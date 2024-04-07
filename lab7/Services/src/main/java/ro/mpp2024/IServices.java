package ro.mpp2024;

import java.sql.SQLException;
import java.time.LocalTime;

public interface IServices {
    boolean handleLogin(String username, String password, IObserver client) throws Exception;
    void logout(Agentie user, IObserver client) throws Exception;
    Iterable<Excursie> getAllExcursii();
    Iterable<Excursie> getExcursiiBetweenHours(String obiectiv, LocalTime ora1, LocalTime ora2);
    Integer getFreeSeats(int id) throws SQLException ;
    void addRezervare( Excursie ex, String nume, String telefon, int bilet);

}
