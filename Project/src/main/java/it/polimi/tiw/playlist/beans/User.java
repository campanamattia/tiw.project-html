package it.polimi.tiw.playlist.beans;


public class User{
	private String userName;
    private String password;

    public User(){
        this.userName= null;
        this.password= null;
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return this.userName;
    }
    public String getPassword() {
        return this.password;
    }

    public void setUsernName(String userName) {
        this.userName = userName;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}