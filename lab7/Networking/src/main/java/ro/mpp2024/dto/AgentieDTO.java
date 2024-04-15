package ro.mpp2024.dto;

import java.io.Serializable;


public class AgentieDTO implements Serializable{

    private long id;
    private String username;

    private String password;



    public AgentieDTO(String user, String pass) {
        this.username = user;
        this.password = pass;
    }

    public long getID(){
        return id;
    }

    public void setID(long id){
        this.id = id;
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



    public String getPass() {
        return password;
    }
}
