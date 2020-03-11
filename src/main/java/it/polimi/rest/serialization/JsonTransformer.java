package it.polimi.rest.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.rest.responses.Response;
import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer {

    private final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeHierarchyAdapter(Response.class, new ResponseJsonSerializer())
            .create();

    @Override
    public String render(Object model) {
        return gson.toJson(model);
    }

}
