package it.polimi.rest.communication;

import it.polimi.rest.adapters.Deserializer;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.models.Token;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.sessions.SessionsManager;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.servlet.MultipartConfigElement;
import java.util.Optional;

public class Responder<T> implements Route {

    private final SessionsManager sessionsManager;
    private final Deserializer<T> deserializer;
    private final Action<T> action;

    public Responder(SessionsManager sessionsManager, Deserializer<T> deserializer, Action<T> action) {
        this.sessionsManager = sessionsManager;
        this.deserializer = deserializer;
        this.action = action;
    }

    @Override
    public Object handle(Request request, Response response) {
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
        request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

        T data = deserializer.parse(request);
        Optional<Token> token = authenticate(request);
        Message message = action.run(data, token.orElse(new Token(new TokenId(null), 0, null, null)));

        response.status(message.code());
        response.type(message.type());

        Optional<Object> payload = message.payload();
        return payload.orElse("");
    }

    private Optional<Token> authenticate(Request request) {
        Optional<String> authenticationHeader = Optional.ofNullable(request.headers("Authorization"));

        if (!authenticationHeader.isPresent()) {
            return Optional.empty();
        }

        String authorization = authenticationHeader.get();

        if (!authorization.startsWith("Bearer")) {
            return Optional.empty();
        }

        String tokenId = authorization.substring("Bearer".length()).trim();
        return sessionsManager.get(new TokenId(tokenId));
    }

    public interface Action<T> {
        Message run(T payload, Token token);
    }

}
