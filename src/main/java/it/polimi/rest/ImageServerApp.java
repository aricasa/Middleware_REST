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
        init();
    }

    public void init() {
        ResponseTransformer jsonTransformer = new JsonTransformer();

        exception(RestException.class, (exception, request, response) -> {
            response.status(exception.code);
            response.type("application/json");

            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();

            response.body(gson.toJson(exception));
        });

        exception(Exception.class, (exception, request, response) -> exception.printStackTrace());

        get("/", imageServerAPI.root(), jsonTransformer);

        path("/users", () -> {
            get("", imageServerAPI.users(), jsonTransformer);
            post("", imageServerAPI.signup(), jsonTransformer);

            path("/:username", () -> {
                get("", imageServerAPI.userByUsername(":username"), jsonTransformer);
                delete("", imageServerAPI.deleteUser(":username"), jsonTransformer);
            });
        });

        path("/sessions", () -> {
            post("", imageServerAPI.login(), jsonTransformer);
            // TODO: get session details
            delete("/:tokenId", imageServerAPI.logout(":tokenId"), jsonTransformer);
        });

        path("/users/:username/images", () -> {
            get("", imageServerAPI.userImages(":username"), jsonTransformer);
            post("", imageServerAPI.newImage(":username"), jsonTransformer);

            path("/:imageId", () -> {
                get("", imageServerAPI.getImageDetails(":imageId"), jsonTransformer);
                get("/raw", imageServerAPI.getImageRaw(":imageId"));
                delete("", imageServerAPI.deleteImage(":imageId"), jsonTransformer);
            });
        });
    }

}

