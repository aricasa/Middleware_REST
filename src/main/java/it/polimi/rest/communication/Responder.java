package it.polimi.rest.communication;

import it.polimi.rest.adapters.Deserializer;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.exceptions.RedirectionException;
import it.polimi.rest.exceptions.UnauthorizedException;
import it.polimi.rest.models.TokenId;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.servlet.MultipartConfigElement;
import java.util.Optional;

public class Responder<T> implements Route {

    private final TokenExtractor tokenExtractor;
    private final Deserializer<T> deserializer;
    private final Action<T> action;

    public Responder(TokenExtractor tokenExtractor, Deserializer<T> deserializer, Action<T> action) {
        this.tokenExtractor = tokenExtractor;
        this.deserializer = deserializer;
        this.action = action;
    }

    @Override
    public Object handle(Request request, Response response) {
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
        request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

        try {
            TokenId token = tokenExtractor == null ? null : tokenExtractor.extract(request);
            T data = deserializer.parse(request);
            Message message = action.run(data, token);

            response.status(message.code());
            response.type(message.type());

            Optional<Object> payload = message.payload();
            return payload.orElse("");

        } catch (UnauthorizedException e) {
            response.header("WWW-Authenticate", e.authentication.toString());
            throw e;

        } catch (RedirectionException e) {
            response.redirect(e.url);
            return null;
        }
    }

    public interface Action<R> {
        Message run(R data, TokenId token);
    }

}
