package ro.mpp2024;

import java.time.LocalTime;

public class Excursie extends Entitate {
    private String obiectivTuristic;
    private String numeTransport;
    private LocalTime oraPlecare;
    private Integer pret;
    private Integer nrLocuri;

    private Integer locuriLibere;

    public Excursie(String obiectivTuristic, String numeTransport, LocalTime oraPlecare, Integer pret, Integer nrLocuri, Integer locuriLibere) {
        this.obiectivTuristic = obiectivTuristic;
        this.numeTransport = numeTransport;
        this.oraPlecare = oraPlecare;
        this.pret = pret;
        this.nrLocuri = nrLocuri;
        this.locuriLibere = locuriLibere;
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

    public Integer getPret() {
        return pret;
    }

    public void setPret(Integer pret) {
        this.pret = pret;
    }

    public Integer getNrLocuri() {
        return nrLocuri;
    }

    public void setNrLocuri(Integer nrLocuri) {
        this.nrLocuri = nrLocuri;
    }

    public Integer getLocuriLibere() {
        return locuriLibere;
    }

    public void setLocuriLibere(Integer locuriLibere) {
        this.locuriLibere = locuriLibere;
    }

    @Override
    public String toString() {
        return "Excursie{" +
                "obiectivTuristic='" + obiectivTuristic + '\'' +
                ", numeTransport='" + numeTransport + '\'' +
                ", oraPlecare=" + oraPlecare +
                ", pret=" + pret +
                ", nrLocuri=" + nrLocuri +
                ", locuriLibere=" + locuriLibere +
                '}';
    }
}
