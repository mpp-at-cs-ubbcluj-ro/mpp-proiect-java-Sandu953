package ro.mpp2024;

import ro.mpp2024.model.Excursie;
import ro.mpp2024.start.RestTest;
import ro.mpp2024.start.ServiceException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        RestTest restTest = new RestTest();

        try {
            System.out.println(Arrays.toString(restTest.getAll()));
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        try {
            System.out.println(restTest.getById("1"));
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        try {
            Excursie excursie = new Excursie("Maramures", "TCP", LocalTime.of( 12,0,0), 100,100,50);
            excursie.setId(100L);
            System.out.println(restTest.create(excursie));
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        try {
            Excursie excursie = new Excursie("Maramures", "TRANSURBIS", LocalTime.of( 12,0,0), 100,100,50);
            excursie.setId(100L);
            restTest.update(excursie);

            System.out.println(Arrays.toString(restTest.getAll()));
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        try {
            restTest.delete("100");
            System.out.println(Arrays.toString(restTest.getAll()));
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    
    }
}