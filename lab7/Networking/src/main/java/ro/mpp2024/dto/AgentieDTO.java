package ro.mpp2024.dto;

import java.io.Serializable;


public class AgentieDTO implements Serializable{
    private String username;



    public AgentieDTO(String user) {
        this.username = user;
    }

    public String getUser() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    @Override
    public String toString(){
        return "UserDTO["+username+"]";
    }
}
