package it.polimi.rest.communication;

import it.polimi.rest.adapters.Deserializer;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.exceptions.RedirectionException;
import it.polimi.rest.exceptions.UnauthorizedException;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.utils.Logger;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.servlet.MultipartConfigElement;
import java.util.Optional;
import java.util.function.Function;

public abstract class Responder<T extends TokenId, D> implements Route {

    protected final Logger logger = new Logger(getClass());

    protected abstract Optional<TokenExtractor<T>> tokenExtractor();

    protected abstract D deserialize(Request request);

    protected abstract Message process(T token, D data);

    @Override
    public Object handle(Request request, Response response) {
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
        request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

        try {
            T token = tokenExtractor()
                    .map(extractor -> extractor.extract(request))
                    .orElse(null);

            D data = deserialize(request);
            Message message = process(token, data);

            response.status(message.code());
            response.type(message.type());

            message.cacheControl().ifPresent(policy ->
                    response.header("Cache-Control", policy));

            message.pragma().ifPresent(value ->
                    response.header("Pragma", value));

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

}
