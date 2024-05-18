package ro.mpp2024;

import javax.persistence.*;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@AttributeOverrides({
        @AttributeOverride(name="id", column = @Column(name="ID"))
})
@Table(name="Agentie")
public class Agentie extends Entitate implements java.io.Serializable{

    @Column(name="USERNAME")
    private String username;
    @Column(name="PASSWORD")
    private String password;

    public Agentie(String username) {
        this.username = username;
    }

    public Agentie() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Agentie{" +
                "username='" + username + '\'' +
                '}';
    }
}
