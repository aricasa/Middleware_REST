package it.polimi.rest.responses;

import spark.Request;
import spark.Route;

public class Responder implements Route {

    private final Action action;

    private Responder(Action action) {
        this.action = action;
    }

    public static Responder build(Action action) {
        return new Responder(action);
    }

    @Override
    public Object handle(Request request, spark.Response response) {
        Response message = action.run(request);
        response.status(message.code);
        response.type(message.type);
        return message;
    }

    public interface Action {
        Response run(Request request);
    }

}
