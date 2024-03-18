package ro.mpp2024.domain;

public class Rezervare extends Entity<Long>{
    private Excursie excursie;
    private String numeClient;
    private String nrTelefon;
    private Integer nrLocuri;

    public Rezervare(Excursie excursie, String numeClient, String nrTelefon, Integer nrLocuri) {
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

    public String getNrTelefon() {
        return nrTelefon;
    }

    public void setNrTelefon(String nrTelefon) {
        this.nrTelefon = nrTelefon;
    }

    public Integer getNrLocuri() {
        return nrLocuri;
    }

    public void setNrLocuri(Integer nrLocuri) {
        this.nrLocuri = nrLocuri;
    }

    @Override
    public String toString() {
        return "Rezervare{" +
                "excursie=" + excursie +
                ", numeClient='" + numeClient + '\'' +
                ", nrTelefon='" + nrTelefon + '\'' +
                ", nrLocuri=" + nrLocuri +
                '}';
    }
}
