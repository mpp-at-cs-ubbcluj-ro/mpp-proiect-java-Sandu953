package ro.mpp2024;

import ro.mpp2024.Excursie;

import java.sql.SQLException;
import java.time.LocalTime;

public interface IExcursieRepo extends IRepository<Integer, Excursie> {
    Iterable<Excursie> findExcursieBetweenHours(LocalTime ora1, LocalTime ora2, String obiectiv);

    Integer findLocuriLibere(int id) throws SQLException;
}
