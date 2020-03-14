package it.polimi.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.rest.exceptions.RestException;
import it.polimi.rest.serialization.JsonTransformer;
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

        path("/users", () -> {
            get("", imageServerAPI.users(), jsonTransformer);
            post("", imageServerAPI.signup(), jsonTransformer);

            get("/:username", imageServerAPI.username(":username"), jsonTransformer);
            delete("/:username", imageServerAPI.deleteUser(":username"), jsonTransformer);

            // TODO: update user data
        });

        path("/session", () -> {
            post("", imageServerAPI.login(), jsonTransformer);
            delete("", imageServerAPI.logout(), jsonTransformer);
        });

        path("/users/:username/images", () -> {
            get("", imageServerAPI.userImages(":username"), jsonTransformer);
            post("", imageServerAPI.newImage(":username"), jsonTransformer);
            get("/:imageId", imageServerAPI.getImageDetails(":username", ":imageId"), jsonTransformer);
            get("/:imageId/raw", imageServerAPI.getImageRaw(":username", ":imageId"));
            delete("/:imageId", imageServerAPI.deleteImage(":username", ":imageId"), jsonTransformer);
        });
    }

}

