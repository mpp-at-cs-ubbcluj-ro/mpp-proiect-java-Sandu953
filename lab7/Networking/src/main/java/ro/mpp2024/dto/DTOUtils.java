package ro.mpp2024.dto;


import ro.mpp2024.Agentie;
import ro.mpp2024.Excursie;
import ro.mpp2024.Rezervare;

import java.sql.Timestamp;
import java.time.LocalTime;

public class DTOUtils {
    public static Agentie getFromDTO(AgentieDTO agentieDTO){
        String user=agentieDTO.getUser();
        return new Agentie(user);
    }
    public static AgentieDTO getDTO(Agentie agentie){
        String user=agentie.getUsername();
        return new AgentieDTO(user,"");
    }

    public static Excursie getFromDTO(ExcursieDTO excursieDTO){
        String obiectiv=excursieDTO.getObiectiv();
        String firmaTransport=excursieDTO.getFirmaTransport();
        LocalTime oraPlecare=excursieDTO.getOraPlecare();
        Integer nrLocuri=excursieDTO.getNrLocuri();
        Integer pret=excursieDTO.getPret();
        Integer locuriLibere=excursieDTO.getLocuriLibere();
        return new Excursie(obiectiv, firmaTransport, oraPlecare,  pret, nrLocuri, locuriLibere);
    }

    public static ExcursieDTO getDTO(Excursie excursie){
        String obiectiv=excursie.getObiectivTuristic();
        String firmaTransport=excursie.getNumeTransport();
        LocalTime oraPlecare=excursie.getOraPlecare();
        Integer nrLocuri=excursie.getNrLocuri();
        Integer pret=excursie.getPret();
        Integer locuriLibere=excursie.getLocuriLibere();
        return new ExcursieDTO(obiectiv, firmaTransport, oraPlecare, nrLocuri, pret, locuriLibere);
    }

    public static RezervareDTO getDTO(Rezervare rezervare){
        long idE=rezervare.getExcursie();
        String numeClient=rezervare.getNumeClient();
        String nrTelefon=rezervare.getNrTelefon();
        Integer nrBilete=rezervare.getNrLocuri();
        return new RezervareDTO(idE, numeClient,nrTelefon, nrBilete);
    }

    public static Rezervare getFromDTO(RezervareDTO rezervareDTO){
        long id=rezervareDTO.getIdExcursie();
        String numeClient=rezervareDTO.getNumeClient();
        String nrTelefon=rezervareDTO.getNrTelefon();
        Integer nrBilete=rezervareDTO.getNrBilete();
        return new Rezervare(id, numeClient,nrTelefon, nrBilete);
    }

    public static AgentieDTO[] getDTO(Agentie[] agenties){
        AgentieDTO[] frDTO=new AgentieDTO[agenties.length];
        for(int i=0;i<agenties.length;i++)
            frDTO[i]=getDTO(agenties[i]);
        return frDTO;
    }

    public static Agentie[] getFromDTO(AgentieDTO[] agentieDTOS){
        Agentie[] friends=new Agentie[agentieDTOS.length];
        for(int i=0;i<agentieDTOS.length;i++){
            friends[i]=getFromDTO(agentieDTOS[i]);
        }
        return friends;
    }
}
