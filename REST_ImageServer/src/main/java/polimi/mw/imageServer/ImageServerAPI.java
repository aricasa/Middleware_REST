package polimi.mw.imageServer;

import com.google.gson.Gson;

import java.io.File;
import java.util.*;
import static java.util.UUID.randomUUID;

public class ImageServerAPI {

    /** Represents the association between each user and the unique key */
    private static Map<String, User> users = new HashMap<>();

    /** Each user (key) is associated to its user storage */
    public static Map<String, UserStorage> images = new HashMap<>();

    /** Path of the folder which contains the images of users */
    private static String storagePath;

    /* Number of seconds which represent the period of time in which a token released to a user is valid */
    private static int tokenExpirationTimeUsers=1000;

    /* Number of seconds which represent the period of time in which a token released to a third party is valid */
    private static int tokenExpirationTimeThirdParty=1000;

    /** Constructor */
    public ImageServerAPI(String storagePath, int timeUsers, int timeThirdParty)
    {
        this.storagePath=storagePath;
        this.tokenExpirationTimeUsers=timeUsers;
        this.tokenExpirationTimeThirdParty=timeThirdParty;
        File userStorage = new File(storagePath);
        if (!userStorage.isDirectory()) userStorage.mkdir();
    }

    /** User methods */

    /** Returns true if already exists a user with the same id or same username */
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

    /** Returns the User (if exists) with username and password passed as arguments */
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

    /** Returns the user if exists and if the token is valid with id passed as asrgument */
    public static User user(String uuid, String token) {
        if(checkCredentialsUser(uuid,token))
            return users.get(uuid);
        return null;
    }


    /**
     *  @param user         user to be added
     *  @param uuid         id of the user
     *
     *  Add the user with a certain id passed as argument */
    public static boolean addID(String uuid, User user) {

        if(existsUser(user))
            return false;

        File userStorage = new File(storagePath+"/"+uuid);
        if (!userStorage.isDirectory()) userStorage.mkdir();
            user.setId(uuid);
        users.put(uuid, user);
        return true;
    }


    /**
     * @param user         user to be added
     *
     * Add the user with id randomly calculated */
    public static boolean add(User user) {
        String generatedId;
        do {
            generatedId=randomUUID().toString().split("-")[0];
        } while(users.containsKey(generatedId));

        return addID(generatedId, user);
    }


    /**
     *  @param uuid         id of the user to be removed
     *  @param token        authorization token necessary for removing the user
     *
     *  Removes the user passed as argument */
    public static User remove(String uuid, String token) {
        if(checkCredentialsUser(uuid,token))
            return users.remove(uuid);
        return null;
    }


    /** @return the entire list of users registered */
    public static Collection<User> users() {
        return users.values();
    }

    public static int getTokenExpirationTimeThirdParty() { return tokenExpirationTimeThirdParty; }

    public static int getTokenExpirationTimeUsers() { return tokenExpirationTimeUsers; }

    public static void clearUsers() { users.clear(); }

    /** Image methods */


    /**
     *  @param uuid         id of the user
     *  @param token        authorization token necessary for retrieving images
     *
     *  @return  the images of the user specified as argument */
    public static Collection<Image> imagesOfUser(String uuid, String token) {
        if(checkCredentialsUser(uuid,token) && images.get(uuid)!=null && uuid != null) {
            return images.get(uuid).getImages();
        }
        return null;
    }

    /**
     * @param uuid          id of the user
     * @param key           id of the image to be retrieved
     *
     * @return the image in the usare storage of user id which has key id */
    private static Image image(String uuid,String key) {return images.get(uuid).getImage(key);}

    /**
     * @param user          id of the user
     * @param img           image to be added to the storage of user
     * @param token         authorization token necessary for adding an image
     *
     * Add the image to the stotage space of a user
     *
     * @return true if the token is valid, false otherwise */
    public static boolean addImage(String user,Image img, String token) {

        if(checkCredentialsUser(user, token)) {
            String imgID = randomUUID().toString().split("-")[0];
            img.setId(imgID);
            if(images.get(user)==null)
                images.put(user,new UserStorage());
            else
                images.get(user).getStorageSpace().put(imgID, img);
            return true;
        }
        return false;
    }

    /**
     * @param usr           user who is asking to login (consider only username and password)
     *
     * @return the user if the credentials (username and password) given as arguments match a user
     *          null otherwise */
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

    /** @return true if the token is registered to the user (as user token) with id uuid and isn't expired yet */
    public static boolean checkCredentialsUser(String uuid, String token)
    {
        User user=users.get(uuid);
        return user!=null && user.hasToken(token);
    }

    /** @return true if the token is registered to the user (as third party token) with id uuid and isn't expired yet */
    public static boolean checkThirdPartyCredentials(String uuid, String token)
    {
        User user=users.get(uuid);
        return user!=null && (user.hasThirdPartyToken(token) || user.hasToken(token));
    }

    public static String getStoragePath() { return storagePath; }

    public static void clearImages() {

        for(int i=0;i < images.size();i++)
            images.get(i).clearStorage();
        images.clear(); }
}
