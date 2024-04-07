package ro.mpp2024.dto;
import java.io.Serializable;
import java.time.LocalTime;

public class ExcursieDTO implements Serializable{

    private String obiectiv;
    private String firmaTransport;
    private LocalTime oraPlecare;
    private Integer nrLocuri;
    private Integer pret;

    private Integer locuriLibere;

    public ExcursieDTO( String obiectiv, String firmaTransport, LocalTime oraPlecare, Integer nrLocuriDisponibile, Integer pret, Integer locuriLibere) {
        this.obiectiv = obiectiv;
        this.firmaTransport = firmaTransport;
        this.oraPlecare = oraPlecare;
        this.nrLocuri = nrLocuriDisponibile;
        this.pret = pret;
        this.locuriLibere = locuriLibere;
    }



    public String getObiectiv() {
        return obiectiv;
    }

    public String getFirmaTransport() {
        return firmaTransport;
    }

    public LocalTime getOraPlecare() {
        return oraPlecare;
    }

    public Integer getNrLocuri() {
        return nrLocuri;
    }

    public Integer getPret() {
        return pret;
    }

    public Integer getLocuriLibere() {
        return locuriLibere;
    }


    @Override
    public String toString(){
        return "ExcursieDTO["+obiectiv+' '+firmaTransport+' '+oraPlecare.toString()+' '+ nrLocuri.toString()+' '+pret.toString()+ " "+locuriLibere.toString()+"]";
    }

}
