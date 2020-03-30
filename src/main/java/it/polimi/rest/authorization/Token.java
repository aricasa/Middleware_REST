package it.polimi.rest.authorization;

import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.User;

import java.util.Optional;

public interface Token {

    /**
     * Get the ID of the token.
     *
     * @return token ID
     */
    TokenId id();

    /**
     * Get the entity that may have access to the protected resources.
     *
     * @return agent
     */
    Agent agent();

    /**
     * If the token is issued to a user, returns that user.
     *
     * @return user the token was issued to
     */

    Optional<User.Id> user();

    /**
     * Check whether the token is valid.
     * Validity logic may differ among the implementations (i.e. expiration time, etc.)
     *
     * @return validity
     */
    boolean isValid();

}
