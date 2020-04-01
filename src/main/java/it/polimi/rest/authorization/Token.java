package it.polimi.rest.authorization;

import it.polimi.rest.data.DataProvider;
import it.polimi.rest.models.TokenId;

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
     * Check whether the token is valid.
     * Validity logic may differ among the implementations (i.e. expiration time, etc.)
     *
     * @return validity
     */
    boolean isValid();

    /**
     * Called when the token has been recognized as no more valid.
     * The method should leverage the given data provider to remove itself.
     *
     * @param dataProvider  data provider
     */
    default void onExpiration(DataProvider dataProvider) {

    }

}
