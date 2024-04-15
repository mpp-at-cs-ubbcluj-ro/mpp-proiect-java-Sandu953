package ro.mpp2024.dto;
import java.io.Serializable;
import java.time.LocalTime;

public class CautareDTO implements Serializable{
    private String obiectiv;
    private LocalTime ora1;
    private LocalTime ora2;

    public CautareDTO(String obiectiv, LocalTime ora1, LocalTime ora2) {
        this.obiectiv = obiectiv;
        this.ora1 = ora1;
        this.ora2 = ora2;
    }

    public String getObiectiv() {
        return obiectiv;
    }

    public LocalTime getOra1() {
        return ora1;
    }

    public LocalTime getOra2() {
        return ora2;
    }

    @Override
    public String toString(){
        return "CautareDTO["+obiectiv+","+ora1+","+ora2+"]";
    }

}
