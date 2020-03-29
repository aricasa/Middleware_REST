package it.polimi.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.rest.api.MainApi;
import it.polimi.rest.api.OAuth2Api;
import it.polimi.rest.authorization.ACL;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.communication.HttpStatus;
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

    private final MainApi mainApi;
    private final OAuth2Api oAuth2Api;

    public App(MainApi mainApi, OAuth2Api oAuth2Api) {
        this.mainApi = mainApi;
        this.oAuth2Api = oAuth2Api;
    }

    public void start() {
        ResponseTransformer jsonTransformer = new JsonTransformer();

        staticFiles.location("/public");

        exception(RestException.class, (exception, request, response) -> {
            response.status(exception.code);

            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();

            String body = gson.toJson(exception);

            if (body.isEmpty() || body.equals("{}")) {
                response.body("");
            } else {
                response.type("application/json");
                response.body(body);
            }
        });

        exception(Exception.class, (exception, request, response) -> {
            exception.printStackTrace();

            response.status(HttpStatus.INTERNAL_SERVER_ERROR);
            response.body("");
        });

        get("/", mainApi.root(), jsonTransformer);

        path("/users", () -> {
            get("", mainApi.users(), jsonTransformer);
            post("", mainApi.signup(), jsonTransformer);

            path("/:username", () -> {
                get("", mainApi.userByUsername(":username"), jsonTransformer);
                // TODO: update user data
                delete("", mainApi.removeUser(":username"));
            });
        });

        path("/sessions", () -> {
            post("", mainApi.login(), jsonTransformer);
            // TODO: get session details
            delete("/:tokenId", mainApi.logout(":tokenId"), jsonTransformer);
        });

        path("/users/:username/images", () -> {
            get("", mainApi.images(":username"), jsonTransformer);
            post("", mainApi.addImage(":username"), jsonTransformer);

            path("/:imageId", () -> {
                get("", mainApi.imageDetails(":imageId"), jsonTransformer);
                get("/raw", mainApi.imageRaw(":imageId"));
                delete("", mainApi.removeImage(":imageId"));
            });
        });

        path("/users/:username/oauth2/clients", () -> {
            get("", oAuth2Api.clients(":username"), jsonTransformer);
            post("", oAuth2Api.addClient(":username"), jsonTransformer);
            delete("/:clientId", oAuth2Api.removeClient(":clientId"));
        });

        path("/oauth2", () -> {
            get("/authorize", "application/x-www-form-urlencoded", oAuth2Api.authorize());
            post("/grant", "application/x-www-form-urlencoded", oAuth2Api.grant(), jsonTransformer);
            post("/deny", "application/x-www-form-urlencoded", oAuth2Api.deny(), jsonTransformer);
            post("/token", oAuth2Api.token(), jsonTransformer);
        });
    }

    public static void main(String[] args) {
        Authorizer authorizer = new ACL();
        CredentialsManager credentialsManager = new VolatileCredentialsManager();
        SessionsManager sessionsManager = new VolatileSessionManager();
        DataProvider dataProvider = new VolatileDataProvider();

        MainApi mainApi = new MainApi(authorizer, credentialsManager, sessionsManager, dataProvider);
        OAuth2Api oAuth2Api = new OAuth2Api(authorizer, sessionsManager, dataProvider);

        App app = new App(mainApi, oAuth2Api);
        app.start();
    }

}

