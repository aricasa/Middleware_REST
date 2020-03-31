package it.polimi.rest.sessions;

import it.polimi.rest.authorization.Token;
import it.polimi.rest.models.TokenId;

import java.util.function.Supplier;

public interface SessionsManager {

    /**
     * Get a token by its ID.
     *
     * @param id    token ID
     * @return token
     */
    Token token(TokenId id);

    /**
     * Add a new token.
     *
     * @param token     token to be added
     */
    void add(Token token);

    /**
     * Remove a token.
     *
     * @param id    ID of the token to be removed
     */
    void remove(TokenId id);

}
