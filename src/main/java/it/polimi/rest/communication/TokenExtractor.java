package it.polimi.rest.communication;

import it.polimi.rest.models.TokenId;
import spark.Request;

public interface TokenExtractor {

    TokenId extract(Request request);

}
