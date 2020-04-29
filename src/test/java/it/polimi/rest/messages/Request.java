package it.polimi.rest.messages;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

public interface Request {

    default String json() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    default HttpEntity jsonEntity() {
        return new StringEntity(json(), ContentType.APPLICATION_JSON);
    }

}
