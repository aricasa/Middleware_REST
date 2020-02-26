package polimi.mw.imageServer;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

import static spark.Spark.*;

public class ImageServerApp {
    static Logger logger = LoggerFactory.getLogger(ImageServerApp.class);

    public static void main(String[] args) {
        Gson gson = new Gson();

        path("/api", () -> {
            // Authenticate with http basic access authentication
            before("/*", (request, response) -> {
                Boolean authenticated = false;
                String auth = request.headers("Authorization");
                if(auth != null && auth.startsWith("Basic")) {
                    String b64Credentials = auth.substring("Basic".length()).trim();
                    String credentials = new String(Base64.getDecoder().decode(b64Credentials));
                    logger.info("Credentials: "+credentials);
                    if(credentials.equals("admin:admin")) authenticated = true;
                }
                if(!authenticated) {
                    response.header("WWW-Authenticate", "Basic realm=\"Restricted\"");
                    halt(401);
                }
            });
            path("/users", () -> {
                get("", (request, response) -> gson.toJson(ImageServerAPI.users()));     //permette di ottenere tutti gli user
                get("/:id", (request, response) -> {                                        //permette di ottenere le info di un certo user
                    String id = request.params(":id");
                    User s = ImageServerAPI.user(id);
                    if (s != null) {
                        response.status(200);
                        return gson.toJson(s);
                    } else {
                        return gson.toJson(new Response(404, "Not Exists " + id));
                    }
                });
                post("", (request, response) -> {                                       //permette di aggiungere uno user (l'id lo crea il sistema)
                    logger.warn("POST");
                    response.type("application/json");
                    response.status(201);
                    User user = gson.fromJson(request.body(), User.class);
                    ImageServerAPI.add(user);
                    return gson.toJson(new Response(201, "User Created with id [" + user.getId() + "]"));
                });
                put("/:id", (request, response) -> {                                    //permette di aggiungere uno user e specificarne un id
                    logger.warn("PUT");
                    String id = request.params(":id");
                    if (ImageServerAPI.user(id) == null) {
                        response.type("application/json");
                        response.status(201);
                        User user = gson.fromJson(request.body(), User.class);
                        ImageServerAPI.add(id, user);
                        return gson.toJson(new Response(201, "User Created with id [" + user.getId() + "]"));
                    } else {
                        response.status(409);//conflict
                        return gson.toJson(new Response(409, id + " already exists"));
                    }
                });
                delete("/:id", (request, response) -> {                                 //permette di eliminare uno user con un certo id
                    String id = request.params(":id");
                    if (ImageServerAPI.remove(id) != null) {
                        response.status(200);
                        return gson.toJson(new Response(200, "Removed User " + id));
                    } else {
                        return gson.toJson(new Response(404, "Not Exists " + id));
                    }
                });
            });
        });
    }
}

