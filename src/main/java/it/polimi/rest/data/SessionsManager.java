package it.polimi.rest.data;

import it.polimi.rest.authorization.Token;
import it.polimi.rest.models.TokenId;

public interface SessionsManager {

    Token token(TokenId id);
    void add(Token token);
    void remove(TokenId id);

}
