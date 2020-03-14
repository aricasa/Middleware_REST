package it.polimi.rest.messages;

import spark.Request;
import spark.Response;
import spark.Route;

import javax.servlet.MultipartConfigElement;
import java.util.Optional;

public class Responder implements Route {

    private final Action action;

    private Responder(Action action) {
        this.action = action;
    }

    public static Responder build(Action action) {
        return new Responder(action);
    }

    @Override
    public Object handle(Request request, Response response) {
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
        request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

        Message message = action.run(request);

        response.status(message.code());
        response.type(message.type());

        Optional<Object> payload = message.payload();
        return payload.orElse("");
    }

    public interface Action {
        Message run(Request request);
    }

}
