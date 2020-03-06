package polimi.mw.imageServer;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import static spark.Spark.*;

/** Manages the requests from users */

public class ImageServerApp {

    static Logger logger = LoggerFactory.getLogger(ImageServerApp.class);

    static ImageServerAPI imageServerAPI;

    /** Bearer token (when present) in the curl command */
    static String credentials;

    /** Variable set before downloading an image which represents whether
     * the token provided by the user/third party is valid or not */
    static boolean authenticated=true;

    public ImageServerApp(ImageServerAPI imageServerAPI)
    {
        this.imageServerAPI=imageServerAPI;
        Start();
    }

     /** Manages all the requests from client */
     public static void Start() {

        Gson gson = new Gson();

        path("/imageServer", () -> {

            /** Retrieve the token (when present) which will be used in case of request of protected information */
            before("/*", (request, response) -> {
                String auth = request.headers("Authorization");
                if(auth != null && auth.startsWith("Bearer")) {
                    logger.warn(auth);
                      credentials = auth.substring("Bearer".length()).trim();
                      logger.warn(credentials);
                      logger.info("Credentials: "+credentials);
                }
            });

            path("/users", () -> {

                /** This allows to obtain information about all the users
                 * Example of curl command: curl -X GET http://localhost:4567/imageServer/users */
                get("", (request, response) -> gson.toJson(imageServerAPI.users()));


                /** This allows to delete a user with id specified
                * Example of curl command: curl -H "Authorization: Bearer 45ffd34" -X DELETE http://localhost:4567/imageServer/users/123 */
                delete("/:id", (request, response) -> {
                    String id = request.params(":id");
                    if (imageServerAPI.remove(id,credentials) != null) {
                        response.status(200);
                        return gson.toJson(new Response(200, "Removed User " + id+ "\n"));
                    } else {
                        return gson.toJson(new Response(404, "Could not delete user " + id+"\n"));
                    }
                });
            });

            path("/signUp", () -> {

                /** This allows to add a user, whose id is created by the system
                * Example of curl command: curl -X POST http://localhost:4567/imageServer/signUp -H 'Cache-Control: no-cache' -d '{ "name" : "Pippina" , "username" : "piPPone" , "password" : "myPassword"}' */
                post("", (request, response) -> {
                    User user = gson.fromJson(request.body(), User.class);
                    if (imageServerAPI.add(user)) {
                        response.type("application/json");
                        response.status(201);
                        return gson.toJson(new Response(201, "User Created with id [" + user.getId() + "]"));
                    }
                    else {
                        response.status(409);//conflict
                        return gson.toJson(new Response(409, "Username already in use"));
                    }
                });

                /** This allows to add a user, whose id is specified in the command
                * Example of curl command: curl -X PUT http://localhost:4567/imageServer/signUp/123 -H 'Cache-Control: no-cache' -d '{"name":"Maria" , "username" : "piPPone" , "password" : "myPassword"}' */
                put("/:id", (request, response) -> {
                    String id = request.params(":id");
                    User user = gson.fromJson(request.body(), User.class);
                    user.setId(id);
                    if (imageServerAPI.addID(id, user)) {
                        response.type("application/json");
                        response.status(201);
                        return gson.toJson(new Response(201, "User Created with id [" + user.getId() + "]"));
                    } else {
                        response.status(409);//conflict
                        return gson.toJson(new Response(409, "User already exists"));
                    }
                });

            });

            path("/login", () -> {

                /** This allows to login a certain user
                * Example of curl command: curl -X GET http://localhost:4567/imageServer/login -H 'Cache-Control: no-cache' -d '{ "username" : "piPPone" , "password" : "myPassword"}' */
                get("", (request, response) -> {
                    User user = gson.fromJson(request.body(), User.class);
                    String token=imageServerAPI.login(user);
                    if(token!=null) {
                        response.status(200);
                        return gson.toJson(new Response(200, "The token is " + token));
                    }
                    return gson.toJson(new Response(401, "Username and password do not correspond to any user."));
                });
            });

            path("/:id", () -> {

                /** This allows to obtain information about a certain user
                * Example of curl command: curl -H "Authorization: Bearer 53366dd3" -X GET http://localhost:4567/imageServer/0e6a4c00/info */
                get("/info", (request, response) -> {
                    String id = request.params(":id");
                    User s = imageServerAPI.user(id, credentials);
                    if (s != null) {
                        response.status(200);
                        return gson.toJson(s);
                    } else {
                        return gson.toJson(new Response(404, "Could not login user " + id));
                    }
                });

                path("/storageSpace" , () -> {

                    /** This allows to return information about all the images of a certain user
                    * Example of curl command: curl -H "Authorization: Bearer 9e218e81" -X GET http://localhost:4567/imageServer/123/storageSpace */
                    get("", (request, response) -> {
                        String id = request.params(":id");
                        if(imageServerAPI.imagesOfUser(id, credentials)==null && !imageServerAPI.checkCredentialsUser(id,credentials))
                            return gson.toJson(new Response(401, "Wrong token."));
                        return gson.toJson(imageServerAPI.imagesOfUser(id, credentials));
                    });

                    /** Check the Bearer token before downloading an image */
                    before("/*",(request, response) -> {
                        String id = request.params(":id");
                        if(!imageServerAPI.checkThirdPartyCredentials(id,credentials)) {
                            request.raw().removeAttribute("-o");
                            request.raw().removeAttribute("--output");
                            authenticated=false;
                        }
                        else
                            authenticated=true;
                    });

                    /** This allows to download the image with a certain title of a certain user
                    * Example of curl command: curl -H "Authorization: Bearer 9e218e81" -X GET http://localhost:4567/imageServer/123/storageSpace/immagine.jpg --output mypic.jpg */
                    get("/:file", (request, response) -> {

                        if(authenticated) {
                            String fileName = request.params(":file");
                            String id = request.params(":id");
                            String storagePath = ImageServerAPI.getStoragePath();
                            Path filePath = Paths.get(storagePath + "/" + id).resolve(fileName);
                            File file = filePath.toFile();
                            if (file.exists()) {
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

                                response.status(200);
                                return gson.toJson(new Response(200, "File downloaded."));
                            }
                            return gson.toJson(new Response(404, "File doesn't exist."));
                        }
                        return gson.toJson(new Response(404, "Wrong token."));
                    });


                    /** This allows to upload an image in the online storage on a user
                    * Example of curl command: curl -H "Authorization: Bearer 55f6a295" -X POST http://localhost:4567/imageServer/123/storageSpace -F 'file=@/Users/Arianna/Desktop/immagine.jpg' */
                    post("",  (request, response) -> {

                        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement(""));
                        try {

                            Part filePart = request.raw().getPart("file");

                            String uploadedFileName = filePart.getSubmittedFileName();
                            InputStream stream = filePart.getInputStream();
                            Image image=new Image(uploadedFileName);
                            String id = request.params(":id");

                            if(!imageServerAPI.addImage(id,image, credentials))
                                return gson.toJson(new Response(401, "Wrong token."));

                            Files.copy(stream, Paths.get("storage/"+id).resolve(uploadedFileName), StandardCopyOption.REPLACE_EXISTING);


                        } catch (IOException | ServletException e) {
                            return gson.toJson(new Response(404, "Exception occurred while uploading file" + e.getMessage()));
                        }

                        response.status(200);
                        return gson.toJson(new Response(200, "File uploaded."));
                    });
                });

            });

        });
    }


}

