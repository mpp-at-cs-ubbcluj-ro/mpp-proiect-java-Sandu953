package ro.mpp2024;

public class Agentie extends Entity<Long> {
    private String username;

    public Agentie(String username) {
        this.username = username;
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
