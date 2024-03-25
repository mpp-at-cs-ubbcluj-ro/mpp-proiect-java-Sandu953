package ro.mpp2024.service;

import ro.mpp2024.domain.Excursie;
import ro.mpp2024.repository.ExcursieRepo;

import java.sql.SQLException;
import java.time.LocalTime;

public class ServiceExcursie {

    private ExcursieRepo excursieRepo;

    public ServiceExcursie(ExcursieRepo excursieRepo) {
        this.excursieRepo = excursieRepo;
    }

    public void addExcursie(Long id, String obiectiv, String firmaTransport, LocalTime oraPlecare, int nrLocuriDisponibile, int pret, int locuriLibere) {
        Excursie ex = new Excursie(obiectiv, firmaTransport, oraPlecare, nrLocuriDisponibile, pret, locuriLibere);
        ex.setId(id);
        excursieRepo.save(ex);
    }

    public void deleteExcursie(Long id) {
        excursieRepo.delete(id.intValue());
    }

    public Excursie getExcursie(Long id) {
        return excursieRepo.findOne(id.intValue());
    }

    public Iterable<Excursie> getAllExcursii() {
        return excursieRepo.findAll();
    }

    public void updateExcursie(Long id, String obiectiv, String firmaTransport, LocalTime oraPlecare, int nrLocuriDisponibile, int pret, int locuriLibere) {
        Excursie ex = new Excursie(obiectiv, firmaTransport, oraPlecare, nrLocuriDisponibile, pret, locuriLibere);
        ex.setId(id);
        excursieRepo.update(id.intValue(), ex);
    }

    public Iterable<Excursie> getExcursiiBetweenHours(String obiectiv, LocalTime ora1, LocalTime ora2) {
        return excursieRepo.findExcursieBetweenHours(ora1, ora2, obiectiv);
    }

    public Integer getFreeSeats(int id) throws SQLException {
        return excursieRepo.findLocuriLibere(id);
    }

}
