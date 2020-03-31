package it.polimi.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.rest.api.main.ResourcesServer;
import it.polimi.rest.api.oauth2.OAuth2Server;
import it.polimi.rest.authorization.ACL;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.credentials.CredentialsManager;
import it.polimi.rest.credentials.VolatileCredentialsManager;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.data.VolatileDataProvider;
import it.polimi.rest.exceptions.RestException;
import it.polimi.rest.adapters.JsonTransformer;
import it.polimi.rest.sessions.SessionsManager;
import it.polimi.rest.sessions.VolatileSessionManager;
import spark.ResponseTransformer;

import static spark.Spark.*;

public class App {

    private final ResourcesServer resourcesServer;
    private final OAuth2Server oAuth2Server;

    public App(ResourcesServer resourcesServer, OAuth2Server oAuth2Server) {
        this.resourcesServer = resourcesServer;
        this.oAuth2Server = oAuth2Server;
    }

    public void start() {
        ResponseTransformer jsonTransformer = new JsonTransformer();

        staticFiles.location("/public");

        exception(RestException.class, (exception, request, response) -> {
            response.status(exception.code);

            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();

            // Errors should not be cached
            response.header("Cache-Control", "no-store");
            response.header("Pragma", "no-cache");

            // The error message is returned in JSON format
            String body = gson.toJson(exception);

            if (body.isEmpty() || body.equals("{}")) {
                response.body("");
            } else {
                response.type(Message.APPLICATION_JSON);
                response.body(body);
            }
        });

        exception(Exception.class, (exception, request, response) -> {
            exception.printStackTrace();

            // Errors should not be cached
            response.header("Cache-Control", "no-store");
            response.header("Pragma", "no-cache");

            response.status(HttpStatus.INTERNAL_SERVER_ERROR);
            response.body("");
        });

        get("/", resourcesServer.root, jsonTransformer);

        path("/users", () -> {
            get("", resourcesServer.users, jsonTransformer);
            post("", resourcesServer.userAdd, jsonTransformer);

            path("/:username", () -> {
                get("", resourcesServer.userDetails, jsonTransformer);
                // TODO: update user data
                delete("", resourcesServer.userRemove);
            });
        });

        path("/sessions", () -> {
            post("", resourcesServer.login, jsonTransformer);
            // TODO: get session details
            delete("/:tokenId", resourcesServer.logout, jsonTransformer);
        });

        path("/users/:username/images", () -> {
            get("", resourcesServer.userImages, jsonTransformer);
            post("", resourcesServer.imageAdd, jsonTransformer);

            path("/:imageId", () -> {
                get("", resourcesServer.imageDetails, jsonTransformer);
                get("/raw", resourcesServer.imageRaw);
                delete("", resourcesServer.imageRemove);
            });
        });

        path("/users/:username/oauth2/clients", () -> {
            get("", oAuth2Server.clients, jsonTransformer);
            post("", oAuth2Server.clientAdd, jsonTransformer);
            delete("/:clientId", oAuth2Server.clientRemove);
        });

        path("/oauth2", () -> {
            get("/authorize", "application/x-www-form-urlencoded", oAuth2Server.authorize);
            post("/grant", "application/x-www-form-urlencoded", oAuth2Server.grant, jsonTransformer);
            post("/deny", "application/x-www-form-urlencoded", oAuth2Server.deny, jsonTransformer);
            post("/token", oAuth2Server.token, jsonTransformer);
        });
    }

    public static void main(String[] args) {
        Authorizer authorizer = new ACL();
        CredentialsManager credentialsManager = new VolatileCredentialsManager();
        SessionsManager sessionsManager = new VolatileSessionManager();
        DataProvider dataProvider = new VolatileDataProvider();

        ResourcesServer resourcesServer = new ResourcesServer(authorizer, sessionsManager, dataProvider, credentialsManager);
        OAuth2Server oAuth2Server = new OAuth2Server(authorizer, sessionsManager, dataProvider);

        App app = new App(resourcesServer, oAuth2Server);
        app.start();
    }

}

