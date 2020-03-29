package it.polimi.rest.communication;

import it.polimi.rest.models.TokenId;
import spark.Request;

public interface TokenExtractor {

    /**
     * Extract the bearer token ID from a request.
     *
     * @param request   request from which the token has to be extracted
     * @return token ID (null if not found)
     */
    TokenId extract(Request request);

}
