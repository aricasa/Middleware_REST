package it.polimi.rest.communication;

import it.polimi.rest.models.TokenId;
import spark.Request;

public interface TokenExtractor<T extends TokenId> {

    /**
     * Extract the bearer token ID from a request.
     *
     * @param request   request from which the token has to be extracted
     * @return token ID (null if not found)
     */
    T extract(Request request);

}
