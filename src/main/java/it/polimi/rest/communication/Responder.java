package it.polimi.rest.communication;

import it.polimi.rest.adapters.Deserializer;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.exceptions.UnauthorizedException;
import it.polimi.rest.models.TokenId;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.servlet.MultipartConfigElement;
import java.util.Optional;

public class Responder<T> implements Route {

    private final Deserializer<T> deserializer;
    private final Action<T> action;

    public Responder(Deserializer<T> deserializer, Action<T> action) {
        this.deserializer = deserializer;
        this.action = action;
    }

    @Override
    public Object handle(Request request, Response response) {
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
        request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

        try {
            Optional<TokenId> token = authenticate(request);
            T data = deserializer.parse(request, token.orElse(null));
            Message message = action.run(data, token.orElse(null));

            response.status(message.code());
            response.type(message.type());

            Optional<Object> payload = message.payload();
            return payload.orElse("");

        } catch (UnauthorizedException e) {
            response.header("WWW-Authenticate", e.authentication.toString());
            throw e;
        }
    }

    private Optional<TokenId> authenticate(Request request) {
        Optional<String> authenticationHeader = Optional.ofNullable(request.headers("Authorization"));

        if (!authenticationHeader.isPresent()) {
            return Optional.empty();
        }

        String authorization = authenticationHeader.get();

        if (authorization.startsWith("Bearer")) {
            String tokenId = authorization.substring("Bearer".length()).trim();
            return Optional.of(new TokenId(tokenId));

        } else {
            return Optional.empty();
        }
    }

    public interface Action<R> {
        Message run(R data, TokenId token);
    }

}
