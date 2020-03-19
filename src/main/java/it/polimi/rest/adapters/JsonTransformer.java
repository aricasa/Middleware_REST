package it.polimi.rest.adapters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import it.polimi.rest.models.Model;
import spark.ResponseTransformer;

import java.util.Calendar;

/**
 * Create the JSON representation considering only the fields annotated
 * with {@link Expose} and by using the custom adapters.
 */
public class JsonTransformer implements ResponseTransformer {

    private final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeHierarchyAdapter(Model.class, new ModelJsonAdapter())
            .registerTypeHierarchyAdapter(Calendar.class, new CalendarJsonAdapter())
            .create();

    @Override
    public String render(Object model) {
        return gson.toJson(model);
    }

}
