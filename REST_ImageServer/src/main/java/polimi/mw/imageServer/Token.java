package polimi.mw.imageServer;

import java.time.LocalDateTime;

//Represents the Bearer Token released after login (in case of user) or after authorization (in case of third party)

public class Token {

    //String that represents the token
    private String token;

    //Date and time in which the token expires
    private LocalDateTime expirationDate;

    public Token(String token, LocalDateTime expirationDate ) {
        this.token = token;
        this.expirationDate = expirationDate;
    }

    public String getToken() { return token; }

    public boolean isExpired() {
        LocalDateTime time = LocalDateTime.now();
        return time.compareTo(expirationDate) > 0;
    }

    public LocalDateTime getExpirationDate() { return expirationDate; }



}
