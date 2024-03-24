package ro.mpp2024.service;

import ro.mpp2024.domain.Excursie;
import ro.mpp2024.domain.Rezervare;
import ro.mpp2024.repository.RezervareRepo;

public class ServiceRezervare {

    private RezervareRepo repo;

    public ServiceRezervare(RezervareRepo repo) {
        this.repo = repo;
    }

    public void addRezervare( Excursie ex, String nume, String telefon, int bilet) {
        Rezervare rezervare = new Rezervare(ex.getId(), nume, telefon, bilet);
        repo.save(rezervare);
    }

    public void deleteRezervare(Integer id) {
        repo.delete(id);
    }

    public void updateRezervare(Integer id,Excursie ex, String nume, String telefon, int bilet) {
        Rezervare rezervare = new Rezervare(ex.getId(), nume, telefon, bilet);
        repo.update(id, rezervare);
    }

    public Iterable<ro.mpp2024.domain.Rezervare> getAllRezervari() {
        return repo.findAll();
    }

    public ro.mpp2024.domain.Rezervare getRezervare(Integer id) {
        return repo.findOne(id);
    }
}
