package it.polimi.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.rest.exceptions.RestException;
import it.polimi.rest.serialization.JsonTransformer;
import spark.ResponseTransformer;

import static spark.Spark.*;

public class ImageServerApp {

    private final Logger logger = new Logger(this.getClass());
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

        exception(Exception.class, (exception, request, response) -> {
            logger.e(exception.getMessage());
            exception.printStackTrace();
        });

        path("/users", () -> {
            get("", imageServerAPI.users(), jsonTransformer);
            post("", imageServerAPI.signup(), jsonTransformer);

            get("/:username", imageServerAPI.username(":username"), jsonTransformer);
            get("/id/:id", imageServerAPI.userId(":id"), jsonTransformer);

            // TODO: update user data
        });

        path("/session", () -> {
            post("", imageServerAPI.login(), jsonTransformer);
            delete("", imageServerAPI.logout(), jsonTransformer);
        });

        path("/users/:username/images", () -> {
            //get("", imageServerAPI.userImages(":username"), jsonTransformer);
            //post("", imageServerAPI.newImage(":username"), jsonTransformer);
            //get("/:imageId", imageServerAPI.getImage(":username", ":imageId"));
            //delete("/:imageId", imageServerAPI.deleteImage(":username", ":imageId"), jsonTransformer);
        });

        path("/imageServer", () -> {

            /*
            path("/users/:id", () -> {

                // Delete the user
                delete("", ((request, response) -> {
                    String id = request.params(":id");

                    try {
                        imageServerAPI.remove(id, credentials);
                        return Responder.success();
                    } catch (RestException e) {
                        return new Responder(e);
                    }
                }));
            });
            */


            /*
            path("/:id", () -> {
                path("/storageSpace" , () -> {

                    // This allows to return information about all the images of a certain user
                    Example of curl command: curl -H "Authorization: Bearer 9e218e81" -X GET http://localhost:4567/imageServer/123/storageSpace
                    get("", (request, response) -> {
                        String id = request.params(":id");
                        if(imageServerAPI.imagesOfUser(id, credentials)==null && !imageServerAPI.checkCredentialsUser(id,credentials))
                            return gson.toJson(new Responder(401, "Wrong token."));
                        return gson.toJson(imageServerAPI.imagesOfUser(id, credentials));
                    });

                   // Check the Bearer token before downloading an image
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

                    // This allows to download the image with a certain title of a certain user
                    // Example of curl command: curl -H "Authorization: Bearer 9e218e81" -X GET http://localhost:4567/imageServer/123/storageSpace/immagine.jpg --output mypic.jpg
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
                                return gson.toJson(new Responder(200, "File downloaded."));
                            }
                            return gson.toJson(new Responder(404, "File doesn't exist."));
                        }
                        return gson.toJson(new Responder(404, "Wrong token."));
                    });

                    // This allows to upload an image in the online storage on a user
                    // Example of curl command: curl -H "Authorization: Bearer 55f6a295" -X POST http://localhost:4567/imageServer/123/storageSpace -F 'file=@/Users/Arianna/Desktop/immagine.jpg'
                    post("",  (request, response) -> {

                        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement(""));
                        try {

                            Part filePart = request.raw().getPart("file");

                            String uploadedFileName = filePart.getSubmittedFileName();
                            InputStream stream = filePart.getInputStream();
                            Image image=new Image(uploadedFileName);
                            String id = request.params(":id");

                            if(!imageServerAPI.addImage(id,image, credentials))
                                return gson.toJson(new Responder(401, "Wrong token."));

                            Files.copy(stream, Paths.get("storage/"+id).resolve(uploadedFileName), StandardCopyOption.REPLACE_EXISTING);


                        } catch (IOException | ServletException e) {
                            return gson.toJson(new Responder(404, "Exception occurred while uploading file" + e.getMessage()));
                        }

                        response.status(200);
                        return gson.toJson(new Responder(200, "File uploaded."));
                    });
                });

            });
*/

        });
    }


}

