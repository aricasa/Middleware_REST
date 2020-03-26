package it.polimi.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.rest.exceptions.RestException;
import it.polimi.rest.adapters.JsonTransformer;
import spark.ResponseTransformer;

import static spark.Spark.*;

public class ImageServerApp {

    private final ImageServerAPI imageServerAPI;

    public ImageServerApp(ImageServerAPI imageServerAPI) {
        this.imageServerAPI = imageServerAPI;
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

            if (!body.isEmpty() && !body.equals("{}")) {
                response.type("application/json");
                response.body(body);
            }
        });

        exception(Exception.class, (exception, request, response) -> exception.printStackTrace());

        get("/", imageServerAPI.root(), jsonTransformer);

        path("/users", () -> {
            get("", imageServerAPI.users(), jsonTransformer);
            post("", imageServerAPI.signup(), jsonTransformer);

            path("/:username", () -> {
                get("", imageServerAPI.userByUsername(":username"), jsonTransformer);
                // TODO: update user data
                delete("", imageServerAPI.removeUser(":username"));
            });
        });

        path("/sessions", () -> {
            post("", imageServerAPI.login(), jsonTransformer);
            // TODO: get session details
            delete("/:tokenId", imageServerAPI.logout(":tokenId"), jsonTransformer);
        });

        path("/users/:username/images", () -> {
            get("", imageServerAPI.images(":username"), jsonTransformer);
            post("", imageServerAPI.addImage(":username"), jsonTransformer);

            path("/:imageId", () -> {
                get("", imageServerAPI.imageDetails(":imageId"), jsonTransformer);
                get("/raw", imageServerAPI.imageRaw(":imageId"));
                delete("", imageServerAPI.removeImage(":imageId"));
            });
        });

        path("/users/:username/oauth2/clients", () -> {
            get("", imageServerAPI.oAuth2Clients(":username"), jsonTransformer);
            post("", imageServerAPI.addOAuth2Client(":username"), jsonTransformer);
            delete("/:clientId", imageServerAPI.removeOAuth2Client(":clientId"));
        });

        path("/oauth2", () -> {
            get("/authorize", "application/x-www-form-urlencoded", imageServerAPI.oAuth2Authorize());
            post("/authorize", imageServerAPI.oAuth2GrantPermissions(), jsonTransformer);
            post("/token", imageServerAPI.oAuth2Token(), jsonTransformer);
        });
    }

}

