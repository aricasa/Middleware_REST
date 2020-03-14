package it.polimi.rest.models;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface Model {

    Optional<String> self();
    Map<String, Link> links();
    Map<String, Object> embedded();

}
