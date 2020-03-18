package it.polimi.rest.models;

import java.util.*;

public class Root implements Model{

    @Override
    public Optional<String> self() {
        return Optional.of("/");
    }

    @Override
    public Map<String, Link> links() {
        Map<String, Link> links = new HashMap<>();
        links.put("users", new Link("/users"));
        links.put("sessions", new Link("/sessions"));
        return links;
    }

    @Override
    public Map<String, Object> embedded() {
        return null;
    }

}
