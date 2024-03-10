package ro.mpp2024.domain;

public class Rezervare extends Entity<Long>{
    private Excursie excursie;
    private String numeClient;
    private Integer nrTelefon;
    private Integer nrLocuri;

    public Rezervare(Excursie excursie, String numeClient, Integer nrTelefon, Integer nrLocuri) {
        this.excursie = excursie;
        this.numeClient = numeClient;
        this.nrTelefon = nrTelefon;
        this.nrLocuri = nrLocuri;
    }

    public Excursie getExcursie() {
        return excursie;
    }

    public void setExcursie(Excursie excursie) {
        this.excursie = excursie;
    }

    public String getNumeClient() {
        return numeClient;
    }

    public void setNumeClient(String numeClient) {
        this.numeClient = numeClient;
    }

    public Integer getNrTelefon() {
        return nrTelefon;
    }

    public void setNrTelefon(Integer nrTelefon) {
        this.nrTelefon = nrTelefon;
    }

    public Integer getNrLocuri() {
        return nrLocuri;
    }

    public void setNrLocuri(Integer nrLocuri) {
        this.nrLocuri = nrLocuri;
    }
}
