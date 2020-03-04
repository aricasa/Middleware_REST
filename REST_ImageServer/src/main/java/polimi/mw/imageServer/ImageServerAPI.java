package polimi.mw.imageServer;

import java.io.File;
import java.util.*;
import static java.util.UUID.randomUUID;

public class ImageServerAPI {

    private static Map<String, User> users = new HashMap<>();
    private static Map<String, UserStorage> images = new HashMap<>();
    private static int tokenExpirationTimeUsers=1000;
    private static int tokenExpirationTimeThirdParty=1000;
    private static String storagePath;

    //User methods

    public ImageServerAPI(String storagePath)
    {
        this.storagePath=storagePath;
        File userStorage = new File(storagePath);
        if (!userStorage.isDirectory()) userStorage.mkdir();
    }

    private static boolean existsUser(User usr)
    {
        Iterator<User> iterator=users.values().iterator();
        User user;
        while(iterator.hasNext())
        {
            user=iterator.next();
            if(user.getUsername().compareTo(usr.getUsername())==0)
                return true;
        }
        return usr.getId()==null || users.get(usr.getId()) == null ? false : true;
    }

    public static User userWithUsernamePassword(String username, String password)
    {
        Iterator<User> iterator = users().iterator();
        while(iterator.hasNext()) {
            User user = iterator.next();
            if (username.compareTo(user.getUsername()) == 0 && password.compareTo(user.getPassword()) == 0) {
                return user;
            }
        }
        return null;
    }

    public static User user(String uuid, String token) {
        if(checkCredentialsUser(uuid,token))
            return users.get(uuid);
        return null;
    }

    public static boolean addID(String uuid, User user) {

        if(existsUser(user))
            return false;

        File userStorage = new File(storagePath+"/"+uuid);
        if (!userStorage.isDirectory()) userStorage.mkdir();
            user.setId(uuid);
        users.put(uuid, user);
        return true;
    }

    public static boolean add(User user) {
        String generatedId;
        do {
            generatedId=randomUUID().toString().split("-")[0];
        } while(users.containsKey(generatedId));

        return addID(generatedId, user);
    }

    public static User remove(String uuid, String token) {
        if(checkCredentialsUser(uuid,token))
            return users.remove(uuid);
        return null;
    }

    public static Collection<User> users() {
        return users.values();
    }

    public static int getTokenExpirationTimeThirdParty() { return tokenExpirationTimeThirdParty; }

    public static int getTokenExpirationTimeUsers() { return tokenExpirationTimeUsers; }

    //Image methods

    public static Collection<Image> imagesOfUser(String uuid, String token) {
        if(checkCredentialsUser(uuid,token) && images.get(uuid)!=null)
            return images.get(uuid).getImages();
        return null;
    }

    public static Image image(String uuid,String key) {return images.get(uuid).getImage(key);}

    public static boolean addImage(String user,Image img, String token) {
        if(checkCredentialsUser(user, token)) {
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
        Iterator<User> iterator=users.values().iterator();
        while(iterator.hasNext())
        {
            User user=iterator.next();
            if(user.getUsername().compareTo(usr.getUsername())==0 &&
            user.getPassword().compareTo(usr.getPassword())==0)
            {
                return user.addToken(tokenExpirationTimeUsers);
            }
        }
        return null;
    }

    public static boolean checkCredentialsUser(String uuid, String token)
    {
        User user=users.get(uuid);
        return user!=null && user.hasToken(token);
    }

    public static boolean checkThirdPartyCredentials(String uuid, String token)
    {
        User user=users.get(uuid);
        return user!=null && (user.hasThirdPartyToken(token) || user.hasToken(token));
    }

    public static String getStoragePath() { return storagePath; }
}
