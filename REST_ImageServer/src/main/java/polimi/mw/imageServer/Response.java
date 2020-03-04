package polimi.mw.imageServer;

import com.google.gson.JsonElement;


public class Response {

    private int status;
    private String message;
    private JsonElement data;

    public Response(int status, JsonElement data) {
        this.status = status;
        this.data=data;
    }

    public Response(int status, String message) {
        this.status = status;
        this.message=message;
    }
}
