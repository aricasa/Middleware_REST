package it.polimi.rest.interfaces;

import it.polimi.rest.UserStorage;

import java.util.HashMap;
import java.util.Map;

public class VolatileDataProvider {

    /** Each user (key) is associated to its user storage */
    public Map<String, UserStorage> images = new HashMap<>();

}
