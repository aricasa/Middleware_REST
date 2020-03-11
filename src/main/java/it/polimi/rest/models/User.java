package it.polimi.rest.models;

import com.google.gson.annotations.Expose;

public class User extends BaseModel implements Model {

    @Expose(deserialize = false)
    private String id;

    @Expose
    private String username;

    @Expose(serialize = false)
    private String password;

    public User(String id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        return "{" + id + ", " + username + "}";
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
