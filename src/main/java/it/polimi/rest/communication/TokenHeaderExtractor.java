package it.polimi.rest.communication;

import it.polimi.rest.models.TokenId;
import spark.Request;

public class TokenHeaderExtractor implements TokenExtractor {

    @Override
    public TokenId extract(Request request) {
        String authorization = request.headers("Authorization");

        if (authorization == null) {
            return null;
        }

        if (!authorization.startsWith("Bearer")) {
            return null;
        }

        return new TokenId(authorization.substring("Bearer".length()).trim());
    }

}
