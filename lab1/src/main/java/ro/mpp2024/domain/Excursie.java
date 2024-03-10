package ro.mpp2024.domain;

import java.time.LocalTime;

public class Excursie extends Entity<Long>{
    private String obiectivTuristic;
    private String numeTransport;
    private LocalTime oraPlecare;
    private Integer nrLocuri;

    public Excursie(String obiectivTuristic, String numeTransport, LocalTime oraPlecare, Integer nrLocuri) {
        this.obiectivTuristic = obiectivTuristic;
        this.numeTransport = numeTransport;
        this.oraPlecare = oraPlecare;
        this.nrLocuri = nrLocuri;
    }

    public String getObiectivTuristic() {
        return obiectivTuristic;
    }

    public void setObiectivTuristic(String obiectivTuristic) {
        this.obiectivTuristic = obiectivTuristic;
    }

    public String getNumeTransport() {
        return numeTransport;
    }

    public void setNumeTransport(String numeTransport) {
        this.numeTransport = numeTransport;
    }

    public LocalTime getOraPlecare() {
        return oraPlecare;
    }

    public void setOraPlecare(LocalTime oraPlecare) {
        this.oraPlecare = oraPlecare;
    }

    public Integer getNrLocuri() {
        return nrLocuri;
    }

    public void setNrLocuri(Integer nrLocuri) {
        this.nrLocuri = nrLocuri;
    }
}
