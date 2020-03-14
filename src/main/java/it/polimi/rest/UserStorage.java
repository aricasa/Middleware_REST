package it.polimi.rest;

import it.polimi.rest.models.ImageMetadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UserStorage {

    static Map<String, ImageMetadata> storageSpace = new HashMap<>();

    public Map<String, ImageMetadata> getStorageSpace() {
        return storageSpace;
    }

    public Collection<ImageMetadata> getImages() {
        return storageSpace.values();
    }

    public ImageMetadata getImage(String id) {
        return storageSpace.get(id);
    }

    public void clearStorage() {
        storageSpace.clear();
    }

}
