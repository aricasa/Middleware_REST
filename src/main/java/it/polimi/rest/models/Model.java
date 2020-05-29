package it.polimi.rest.models;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface Model {

    default String baseUrl() {
        return "http://localhost:4567";
    }

    Optional<String> self();
    Map<String, Link> links();
    Map<String, Object> embedded();

}
