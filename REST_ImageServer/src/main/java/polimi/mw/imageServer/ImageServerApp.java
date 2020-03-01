package polimi.mw.imageServer;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import polimi.mw.imageServer.Oauth.OauthServer;
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
    static String credentials;

    public static void main(String[] args) {

        //OauthServer authenticationServer = null;
        //authenticationServer.main(null);
        //authenticationServer.setImageServerAPI(ImageServerAPI);

        Gson gson = new Gson();

        path("/imageServer", () -> {
            before("/*", (request, response) -> {
                //Boolean authenticated = false; // Authenticate with http basic access authentication
                String auth = request.headers("Authorization");
                if(auth != null && auth.startsWith("Bearer")) {
                    logger.warn(auth);
                      credentials = auth.substring("Bearer".length()).trim();
                      logger.warn(credentials);
                      //credentials = new String(Base64.getDecoder().decode(b64Credentials));
                      logger.info("Credentials: "+credentials);
                      //if(credentials.equals("esempio"))
                          //authenticated = true;
                }
                //if(!authenticated) {
                  //  response.header("WWW-Authenticate", "Basic realm=\"Restricted\"");
                    //halt(401);
                //}
            });

            path("/login", () -> {

                //This allows to login a certain user
                //Example of curl command: curl -X GET http://localhost:4567/imageServer/login -H 'Cache-Control: no-cache' -d '{ "username" : "piPPone" , "password" : "myPassword"}'
                get("", (request, response) -> {
                    User user = gson.fromJson(request.body(), User.class);
                    return "The token is: "+ ImageServerAPI.login(user);
                });

                    });

            path("/:id", () -> {

                //This allows to obtain information about a certain user
                //Example of curl command: curl -H "Authorization: Bearer 53366dd3" -X GET http://localhost:4567/imageServer/0e6a4c00/info
                get("/info", (request, response) -> {
                    String id = request.params(":id");
                    User s = ImageServerAPI.user(id, credentials);
                    if (s != null) {
                        response.status(200);
                        return gson.toJson(s);
                    } else {
                        return gson.toJson(new Response(404, "Not Exists " + id+"\n"));
                    }
                });

                path("/storageSpace" , () -> {

                    //This allows to return information about all the images of a certain user
                    //Example of curl command: curl -H "Authorization: Bearer 9e218e81" -X GET http://localhost:4567/imageServer/123/storageSpace
                    get("", (request, response) -> {
                        String id = request.params(":id");
                        return gson.toJson(ImageServerAPI.imagesOfUser(id, credentials))+"\n";
                    });

                    //This allows to download the image with a certain title of a certain user
                    //Example of curl command: curl -H "Authorization: Bearer 9e218e81" -X GET http://localhost:4567/imageServer/123/storageSpace/immagine.jpg --output mypic.jpg
                            get("/:file", (request, response) -> {

                                String fileName = request.params(":file");
                                String id = request.params(":id");
                                if(ImageServerAPI.checkThirdPartyCredentials(id,credentials))
                                {
                                    Path filePath = Paths.get("storage/"+id).resolve(fileName);
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

                                }
                                return gson.toJson(new Response(404, "File doesn't exist."));
                            });

                    //This allows to upload an image in the online storage on a user
                    //Example of curl command: curl -H "Authorization: Bearer 55f6a295" -X POST http://localhost:4567/imageServer/123/storageSpace -F 'file=@/Users/Arianna/Desktop/immagine.jpg'
                    post("",  (request, response) -> {
                        // TO allow for multipart file uploads
                        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement(""));
                        try {
                            // "file" is the key of the form data with the file itself being the value
                            Part filePart = request.raw().getPart("file");

                            // The name of the file user uploaded
                            String uploadedFileName = filePart.getSubmittedFileName();
                            InputStream stream = filePart.getInputStream();

                            // Write stream to file under storage folder
                            String id = request.params(":id");
                            Files.copy(stream, Paths.get("storage/"+id).resolve(uploadedFileName), StandardCopyOption.REPLACE_EXISTING);

                            Image image=new Image(uploadedFileName);

                            if(!ImageServerAPI.addImage(id,image, credentials))
                                return gson.toJson(new Response(404, "Wrong token."));

                        } catch (IOException | ServletException e) {
                            return gson.toJson(new Response(404, "Exception occurred while uploading file" + e.getMessage()));
                        }

                        response.status(200);
                        return gson.toJson(new Response(200, "File uploaded."));
                    });
                });

                });

            path("/signUp", () -> {

                //This allows to add a user, whose id is created by the system
                //Example of curl command: curl -X POST http://localhost:4567/imageServer/signUp -H 'Cache-Control: no-cache' -d '{ "name" : "Pippina" , "username" : "piPPone" , "password" : "myPassword"}'
                post("", (request, response) -> {
                    User user = gson.fromJson(request.body(), User.class);
                    if (!ImageServerAPI.existsUser(user)) {
                        response.type("application/json");
                        response.status(201);
                        ImageServerAPI.add(user);
                        return gson.toJson(new Response(201, "User Created with id [" + user.getId() + "]\n"));
                    }
                    else {
                        response.status(409);//conflict
                        return gson.toJson(new Response(409, "Usernmae already in use\n"));
                    }
                });

                //This allows to add a user, whose id is specified in the command
                //Example of curl command: curl -X PUT http://localhost:4567/imageServer/signUp/123 -H 'Cache-Control: no-cache' -d '{"name":"Maria" , "username" : "piPPone" , "password" : "myPassword"}'
                put("/:id", (request, response) -> {
                    String id = request.params(":id");
                    User user = gson.fromJson(request.body(), User.class);
                    user.setId(id);
                    if (!ImageServerAPI.existsUser(user)) {
                        response.type("application/json");
                        response.status(201);
                        ImageServerAPI.add(id, user);
                        return gson.toJson(new Response(201, "User Created with id [" + user.getId() + "]\n"));
                    } else {
                        response.status(409);//conflict
                        return gson.toJson(new Response(409, "Username or key already exists\n"));
                    }
                });

            });

            path("/users", () -> {

                //This allows to obtain information about all the users
                //Example of curl command: curl -X GET http://localhost:4567/imageServer/users
                get("", (request, response) -> gson.toJson(ImageServerAPI.users()));


                //This allows to delete a user with id specified
                //Example of curl command: curl -H "Authorization: Bearer 45ffd34" -X DELETE http://localhost:4567/imageServer/users/123
                delete("/:id", (request, response) -> {
                    String id = request.params(":id");
                    if (ImageServerAPI.remove(id,credentials) != null) {
                        response.status(200);
                        return gson.toJson(new Response(200, "Removed User " + id+ "\n"));
                    } else {
                        return gson.toJson(new Response(404, "Not Exists " + id+"\n"));
                    }
                });
            });

        });
    }


}

