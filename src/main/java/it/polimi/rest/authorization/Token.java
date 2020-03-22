package it.polimi.rest.authorization;

import it.polimi.rest.models.TokenId;

public interface Token extends SecuredObject {

    TokenId id();
    Agent agent();
    boolean isValid();

}
