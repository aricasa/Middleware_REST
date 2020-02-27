package polimi.mw.imageServer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.UUID.randomUUID;

public class ImageServerAPI {

    static Map<String, User> users = new HashMap<>();
    static Map<String, UserStorage> images = new HashMap<>();

    //User methods

    public static User user(String uuid) {
        return users.get(uuid);
    }

    public static User add(String uuid, User user) {
        user.setId(uuid);
        return users.put(uuid, user);
    }

    public static User add(User user) {
        return add(randomUUID().toString().split("-")[0], user);
    }

    public static User remove(String uuid) {
        return users.remove(uuid);
    }

    public static Collection<User> users() {
        return users.values();
    }

    //Image methods

    public static Collection<Image> imagesOfUser(String uuid) {
        if(images.get(uuid)==null) return null;
        return images.get(uuid).getImages();
    }

    public static Image image(String uuid,String key) {return images.get(uuid).getImage(key);}

    public static Image addImage(String user,Image img){ return addImage(user,randomUUID().toString().split("-")[0],img);}

    public static Image addImage(String user,String uuid, Image img) {
        img.setId(uuid);
        if(images.get(user)==null)
            images.put(user,new UserStorage());
        return images.get(user).getStorageSpace().put(uuid, img);
    }

}
