package polimi.mw.imageServer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UserStorage {

    static Map<String, Image> storageSpace = new HashMap<>();

    public Map<String,Image> getStorageSpace() {return storageSpace;}

    public Collection<Image> getImages() {return storageSpace.values();}

    public Image getImage(String id) {return storageSpace.get(id);}
}
