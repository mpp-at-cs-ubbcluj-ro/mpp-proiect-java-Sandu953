package ro.mpp2024.dto;

import java.io.Serializable;

public class RezervareDTO implements Serializable {
    private long idExcursie;
    private String numeClient;
    private String nrTelefon;
    private Integer nrBilete;

    public RezervareDTO( long idExcursie, String client,String nrTel, Integer nrBilete) {
        this.idExcursie = idExcursie;
        this.numeClient = client;
        this.nrTelefon = nrTel;
        this.nrBilete = nrBilete;
    }



    public long getIdExcursie() {
        return idExcursie;
    }

    public String getNumeClient() {
        return numeClient;
    }

    public Integer getNrBilete() {
        return nrBilete;
    }

    public String getNrTelefon() {
        return nrTelefon;
    }

    @Override
    public String toString(){
        return "RezervareDTO["+idExcursie+' '+ numeClient +' '+ nrTelefon+ ' ' +nrBilete+"]";
    }
}
