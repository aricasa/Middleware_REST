package polimi.mw.imageServer;

import java.time.LocalDateTime;
import java.util.Date;

public class Token {

    private String token;
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



}
