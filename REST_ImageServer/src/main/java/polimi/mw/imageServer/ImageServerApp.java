package polimi.mw.imageServer;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Collection;
import java.util.stream.Collectors;

import static spark.Spark.*;

public class ImageServerApp {
    static Logger logger = LoggerFactory.getLogger(ImageServerApp.class);

    public static void main(String[] args) {
        Gson gson = new Gson();

        File storageDir = new File("storage");
        if (!storageDir.isDirectory()) storageDir.mkdir();

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
            path("/users", () ->
            {
                get("", (request, response) -> gson.toJson(ImageServerAPI.users()));     //permette di ottenere tutti gli user
                get("/:id", (request, response) -> {                                        //permette di ottenere le info di un certo user
                    String id = request.params(":id");
                    User s = ImageServerAPI.user(id);
                    if (s != null) {
                        response.status(200);
                        return gson.toJson(s);
                    } else {
                        return gson.toJson(new Response(404, "Not Exists " + id+"\n"));
                    }
                });
                post("", (request, response) -> {                                       //permette di aggiungere uno user (l'id lo crea il sistema)
                    logger.warn("POST");
                    response.type("application/json");
                    response.status(201);
                    User user = gson.fromJson(request.body(), User.class);
                    ImageServerAPI.add(user);
                    return gson.toJson(new Response(201, "User Created with id [" + user.getId() + "]\n"));
                }
                );
                put("/:id", (request, response) -> {                                    //permette di aggiungere uno user e specificarne un id
                    logger.warn("PUT");
                    String id = request.params(":id");
                    if (ImageServerAPI.user(id) == null) {
                        response.type("application/json");
                        response.status(201);
                        User user = gson.fromJson(request.body(), User.class);
                        ImageServerAPI.add(id, user);
                        return gson.toJson(new Response(201, "User Created with id [" + user.getId() + "]\n"));
                    } else {
                        response.status(409);//conflict
                        return gson.toJson(new Response(409, id + " already exists\n"));
                    }
                });
                delete("/:id", (request, response) -> {                                 //permette di eliminare uno user con un certo id
                    String id = request.params(":id");
                    if (ImageServerAPI.remove(id) != null) {
                        response.status(200);
                        return gson.toJson(new Response(200, "Removed User " + id+ "\n"));
                    } else {
                        return gson.toJson(new Response(404, "Not Exists " + id+"\n"));
                    }
                });
            });
            path("/storageSpace" , () -> {
                path("/:id", () -> {                                                    //per ritornare le immagini di un certo user
                    get("", (request, response) -> {
                        String id = request.params(":id");
                        return gson.toJson(ImageServerAPI.imagesOfUser(id))+"\n";
                    });

                    path("/download" , () ->
                            {
                                get("/:file", (request, response) -> {//per ritornare una certa immagine di un certo user
                                    //return "eccoci";
                                    //return downloadFile(request.params(":file"));
                                    logger.warn("qua\n");
                                    String fileName = request.params(":file");
                                    Path filePath = Paths.get("storage").resolve(fileName);
                                    File file = filePath.toFile();
                                    if (file.exists()) {
                                        logger.warn("il file esiste\n");
                                        byte[] data = null;
                                        try {
                                            data = Files.readAllBytes(filePath);
                                        } catch (Exception e1) {

                                            e1.printStackTrace();
                                        }

                                        HttpServletResponse raw = response.raw();
                                        response.header("Content-Disposition", "attachment; filename=" + fileName + ".jpg");
                                        response.type("application/force-download");
                                        try {
                                            raw.getOutputStream().write(data);
                                            raw.getOutputStream().flush();
                                            raw.getOutputStream().close();
                                        } catch (Exception e) {

                                          e.printStackTrace();
                                        }
                                        return "Downloaded.\n";
                                    }
                                    return "File doesn't exist.\n";
                                });
                            }
                            );


                    post("/upload",  (request, response) -> {                           //per aggiungere una immagine nello storage di un certo user
                        // TO allow for multipart file uploads
                        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement(""));
                        try {
                            // "file" is the key of the form data with the file itself being the value
                            Part filePart = request.raw().getPart("file");

                            // The name of the file user uploaded
                            String uploadedFileName = filePart.getSubmittedFileName();
                            InputStream stream = filePart.getInputStream();

                            // Write stream to file under storage folder
                            Files.copy(stream, Paths.get("storage").resolve(uploadedFileName), StandardCopyOption.REPLACE_EXISTING);

                        } catch (IOException | ServletException e) {
                            return "Exception occurred while uploading file" + e.getMessage();
                        }
                        return "File successfully uploaded";
                    });
                });
            });
        });
    }
    

}

