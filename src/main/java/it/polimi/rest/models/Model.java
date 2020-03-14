package it.polimi.rest.models;

import it.polimi.rest.messages.Link;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface Model {

    Optional<String> self();
    Collection<Link> links();
    Map<String, Object> embedded();

}
