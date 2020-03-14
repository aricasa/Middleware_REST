package it.polimi.rest.models;

import com.google.gson.annotations.Expose;

import java.util.*;

// Represents the Bearer Token released after login (in case of user) or after authorization (in case of third party)

public class Token {

    @Expose
    public final String id;

    @Expose(deserialize = false)
    private final Calendar expiration;

    public final User owner;

    public Token(String id, int lifeTime, User owner) {
        this.id = id;

        this.expiration = Calendar.getInstance();
        this.expiration.add(Calendar.SECOND, lifeTime);

        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return id.equals(token.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Check if the token is still valid.
     *
     * @return whether the token is valid (true) or has expired (false)
     */
    public boolean isValid() {
        Calendar now = Calendar.getInstance();
        return now.before(expiration);
    }

}
