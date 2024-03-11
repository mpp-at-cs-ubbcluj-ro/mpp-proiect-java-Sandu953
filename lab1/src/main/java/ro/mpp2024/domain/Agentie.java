package ro.mpp2024.domain;

import java.util.Objects;

public class Agentie extends Entity<Long>{
    private String username;
    private String password;

    public Agentie(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Agentie agentie = (Agentie) o;
        return Objects.equals(username, agentie.username) && Objects.equals(password, agentie.password);
    }
}
