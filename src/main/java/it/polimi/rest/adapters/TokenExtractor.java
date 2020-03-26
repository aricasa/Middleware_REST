package it.polimi.rest.adapters;

import it.polimi.rest.authorization.Token;
import it.polimi.rest.models.TokenId;
import spark.Request;

public interface TokenExtractor {

    TokenId extract(Request request);

}
