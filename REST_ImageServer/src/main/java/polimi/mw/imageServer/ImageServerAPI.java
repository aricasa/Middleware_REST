package polimi.mw.imageServer;

import polimi.mw.imageServer.Oauth.OauthFailedResponse;
import polimi.mw.imageServer.Oauth.OauthRequestToken;
import polimi.mw.imageServer.Oauth.OauthResponseToken;
import polimi.mw.imageServer.Oauth.OauthSuccessfulResponse;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.UUID.randomUUID;

public class ImageServerAPI {

    private static Map<String, User> users = new HashMap<>();
    private static Map<String, UserStorage> images = new HashMap<>();
    private static int tokenExpirationTime=1000;

    //User methods

    public static boolean existsUser(User user)
    {
        for(int i=0;i<users.size();i++)
        {
            if(users.values().iterator().next().getUsername().compareTo(user.getUsername())==0)
                return true;
        }
        return user.getId()==null || users.get(user.getId()) == null ? false : true;
    }

    public static User user(String uuid, String token) {
        if(checkCredentials(uuid,token))
            return users.get(uuid);
        return null;
    }

    public static User add(String uuid, User user) {

        if(users.containsKey(uuid))
            return null;
        File userStorage = new File("storage/"+uuid);
        if (!userStorage.isDirectory()) userStorage.mkdir();
            user.setId(uuid);
        return users.put(uuid, user);
    }

    public static User add(User user) {

        String generatedId;

        do {
            generatedId=randomUUID().toString().split("-")[0];
        } while(users.containsKey(generatedId));

        return add(generatedId, user);
    }

    public static User remove(String uuid, String token) {
        if(checkCredentials(uuid,token))
            return users.remove(uuid);
        return null;
    }

    public static Collection<User> users() {
        return users.values();
    }

    //Image methods

    public static Collection<Image> imagesOfUser(String uuid, String token) {
        if(checkCredentials(uuid,token))
            //if(images.get(uuid)==null)
            return images.get(uuid).getImages();
        return null;
    }

    public static Image image(String uuid,String key) {return images.get(uuid).getImage(key);}

    public static boolean addImage(String user,Image img, String token) {
        if(checkCredentials(user, token)) {
            addImage(user, randomUUID().toString().split("-")[0], img);
            return true;
        }
        return false;
    }

    public static Image addImage(String user,String uuid, Image img) {
        img.setId(uuid);
        if(images.get(user)==null)
            images.put(user,new UserStorage());
        return images.get(user).getStorageSpace().put(uuid, img);
    }

    public static String login(User usr)
    {
        for(int i=0;i<users.values().size();i++)
        {
            User user=users.values().iterator().next();
            if(user.getUsername().compareTo(usr.getUsername())==0 &&
            user.getPassword().compareTo(usr.getPassword())==0)
            {
                return user.addToken(tokenExpirationTime);
            }
        }
        return null;
    }

    private static boolean checkCredentials(String uuid, String token)
    {
        User user=users.get(uuid);
        return user!=null && user.hasToken(token);
    }

    public static OauthResponseToken authenticate(OauthRequestToken requestToken)
    {
        OauthResponseToken responseToken;

        if(users.size()==0)
            return new OauthFailedResponse("zero");

        for(int i=0;i<users.size();i++)
        {
            User user=users.values().iterator().next();
            if(requestToken.getClient_id().compareTo(user.getUsername())==0 && requestToken.getClient_secret().compareTo(user.getPassword())==0)
            {
                responseToken=new OauthSuccessfulResponse();
                ((OauthSuccessfulResponse) responseToken).setAccess_token(user.addThirdPartyToken(tokenExpirationTime));
                return responseToken;
            }
            return new OauthFailedResponse(requestToken.getClient_id() + " " + user.getUsername() + " " + requestToken.getClient_secret() + " " + user.getPassword());
        }

        responseToken= new OauthFailedResponse("Authentication failed.");
        return responseToken;
    }

    public static boolean checkThirdPartyCredentials(String uuid, String token)
    {
        User user=users.get(uuid);
        return user!=null && user.hasThirdPartyToken(token);
    }

}
