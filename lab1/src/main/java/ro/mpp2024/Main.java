package ro.mpp2024;

import ro.mpp2024.domain.Agentie;
import ro.mpp2024.domain.Excursie;
import ro.mpp2024.domain.Rezervare;
import ro.mpp2024.repository.AgentieRepo;
import ro.mpp2024.repository.ExcursieRepo;
import ro.mpp2024.repository.RezervareRepo;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalTime;
import java.util.Properties;


// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException {

        Properties props=new Properties();
        try {
            props.load(new FileReader("bd.config"));
        } catch (IOException e) {
            System.out.println("Cannot find bd.config "+e);
        }

        SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();



        AgentieRepo agentieRepo = new AgentieRepo(props, secretKey);
        Agentie ag1 = new Agentie("A1", "12345");
        ag1.setId(1L);
        Agentie ag2 = new Agentie("A2", "12345");
        ag2.setId(2L);
        agentieRepo.save(ag1);
        agentieRepo.save(ag2);
        System.out.println("Toate agentiile din db");
        for(Agentie ag:agentieRepo.findAll())
            System.out.println(ag);
        System.out.println(agentieRepo.size());
        System.out.println(agentieRepo.findOne(1));
        agentieRepo.update(1,new Agentie("A3", "12345"));
        System.out.println("Update");
        for(Agentie ag:agentieRepo.findAll())
            System.out.println(ag);
        agentieRepo.delete(1);
        agentieRepo.delete(2);

        ExcursieRepo excursieRepo = new ExcursieRepo(props);
        LocalTime time = LocalTime.parse("12:00:00");
        Excursie ex1 = new Excursie("A1", "D1", time, 10, 10);
        ex1.setId(1L);
        Excursie ex2 = new Excursie("A2", "D2", time, 10, 10);
        ex2.setId(2L);
//        excursieRepo.save(ex1);
//        excursieRepo.save(ex2);
//        System.out.println("Toate excursiile din db");
//        for(Excursie ex:excursieRepo.findAll())
//            System.out.println(ex);
//        System.out.println(excursieRepo.size());
//        System.out.println(excursieRepo.findOne(1));
//        excursieRepo.update(1,new Excursie("A3", "D3", time, 10, 10));
//        System.out.println("Update");
//        for(Excursie ex:excursieRepo.findAll())
//            System.out.println(ex);
//        excursieRepo.delete(1);
//        for(Excursie ex:excursieRepo.findAll())
//            excursieRepo.delete(ex.getId().intValue());


        RezervareRepo rezervareRepo = new RezervareRepo(props, excursieRepo);
        Rezervare rez = new Rezervare( ex2, "Nume", "12345", 10);
        rez.setId(1L);
        rezervareRepo.save(rez);
        Rezervare rez2 = new Rezervare( ex2, "Nume2", "12345", 10);
        rez2.setId(2L);
        rezervareRepo.save(rez2);
        System.out.println("Toate rezervarile din db");
        for(Rezervare re:rezervareRepo.findAll())
            System.out.println(re);
        System.out.println(rezervareRepo.size());
        System.out.println(rezervareRepo.findOne(1));
        rezervareRepo.update(1,new Rezervare( ex2, "Nume3", "12345", 10));
        for(Rezervare re:rezervareRepo.findAll())
            System.out.println(re);
        rezervareRepo.delete(1);
        rezervareRepo.delete(2);
    }
}
