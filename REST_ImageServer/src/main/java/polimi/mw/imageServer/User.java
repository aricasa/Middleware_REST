package polimi.mw.imageServer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import static java.util.UUID.randomUUID;

public class User {

    private String id;
    private String name;
    private String username;
    private String password;
    private ArrayList<Token> tokens= new ArrayList<Token>();
    private ArrayList<Token> thirdPartyTokens= new ArrayList<Token>();

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {this.name=name;}

    public String getId() {
        return id;
    }

    public String getName() {return name;}

    public String getUsername() {return username;}

    public String getPassword() {return password;}

    public void addToken(Token token, int exprirationTime) {}

    public String addToken(int exprirationTime)
    {
        String token=randomUUID().toString().split("-")[0];
        LocalDateTime expirationDate=LocalDateTime.now();
        expirationDate=expirationDate.plusSeconds(exprirationTime);
        tokens.add(new Token(token,expirationDate));
        return token;
    }

    public String addThirdPartyToken(int exprirationTime)
    {
        String token=randomUUID().toString().split("-")[0];
        LocalDateTime expirationDate=LocalDateTime.now();
        expirationDate=expirationDate.plusSeconds(exprirationTime);
        thirdPartyTokens.add(new Token(token,expirationDate));
        return token;
    }

    public boolean hasThirdPartyToken(String token) {

        for(int i=0;i<thirdPartyTokens.size();i++)
        {
            if(thirdPartyTokens.get(i).getToken().compareTo(token)==0 && !thirdPartyTokens.get(i).isExpired())
                return true;
        }
        return false;

    }

    public boolean hasToken(String token)
    {
        for(int i=0;i<tokens.size();i++)
        {
            if(tokens.get(i).getToken().compareTo(token)==0 && !tokens.get(i).isExpired())
                return true;
        }
        return false;
    }
}
