package polimi.mw.imageServer;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static java.util.UUID.randomUUID;

public class User {

    private String id;
    private String name;
    private String username;
    private String password;
    private ArrayList<Token> tokens= new ArrayList<Token>();
    private ArrayList<Token> thirdPartyTokens= new ArrayList<Token>();

    public User(String id, String name, String username, String password, ArrayList<Token> tokens, ArrayList<Token> thirdPartyTokens )
    {
        this.id=id;
        this.name=name;
        this.username=username;
        this.password=password;
        this.tokens=tokens;
        this.thirdPartyTokens=thirdPartyTokens;
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
        if(tokens==null)
            tokens= new ArrayList<Token>();
        String token=randomUUID().toString().split("-")[0];
        LocalDateTime expirationDate=LocalDateTime.now();
        expirationDate=expirationDate.plusSeconds(exprirationTime);
        tokens.add(new Token(token,expirationDate));
        return token;
    }

    public String addThirdPartyToken(int exprirationTime)
    {
        if(thirdPartyTokens==null)
            thirdPartyTokens=new ArrayList<Token>();
        String token=randomUUID().toString().split("-")[0];
        LocalDateTime expirationDate=LocalDateTime.now();
        expirationDate=expirationDate.plusSeconds(exprirationTime);
        thirdPartyTokens.add(new Token(token,expirationDate));
        return token;
    }

    public boolean hasThirdPartyToken(String token) {

        if(thirdPartyTokens==null)
            return false;

        for(int i=0;i<thirdPartyTokens.size();i++)
        {
            if(thirdPartyTokens.get(i).getToken().compareTo(token)==0 && !thirdPartyTokens.get(i).isExpired())
            {
                System.out.println(thirdPartyTokens.get(i).getToken() + " uguale a " + token);
                return true;
            }

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
