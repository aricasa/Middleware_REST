package it.polimi.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.authorization.Token;
import it.polimi.rest.credentials.CredentialsManager;
import it.polimi.rest.exceptions.NotFoundException;
import it.polimi.rest.exceptions.UnauthorizedException;
import it.polimi.rest.models.User;
import it.polimi.rest.responses.*;
import spark.Request;
import spark.Route;

import java.util.*;

public class ImageServerAPI {

    private final Logger logger = new Logger(this.getClass());

    private final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    private final CredentialsManager credentialsManager;
    private final Authorizer authorizer;

    /**
     * Constructor.
     *
     * @param credentialsManager    credentials manager
     */
    public ImageServerAPI(CredentialsManager credentialsManager, Authorizer authorizer) {
        this.credentialsManager = credentialsManager;
        this.authorizer = authorizer;
    }

    public Route users() {
        return Responder.build(request -> {
            authenticate(request);
            Collection<User> users = credentialsManager.users();
            return new UsersListResponse(users);
        });
    }

    public Route signup() {
        return Responder.build(request -> {
            User user = gson.fromJson(request.body(), User.class);
            User registered = credentialsManager.signup(user);
            logger.d("User " + registered + " created");
            return new SignupResponse(registered);
        });
    }

    public Route login() {
        return Responder.build(request -> {
            User user = gson.fromJson(request.body(), User.class);
            Token token = credentialsManager.login(user);
            logger.d("User " + token.owner + " logged in");
            return new LoginResponse(token);
        });
    }

    public Route logout() {
        return Responder.build(request -> {
            User user = authenticate(request);
            authorizer.revoke(user);
            logger.d("User " + user + " logged out");
            return new LogoutResponse();
        });
    }

    public Route username(String usernameParam) {
        return Responder.build(request -> {
            authenticate(request);
            String username = request.params(usernameParam);
            Optional<User> user = credentialsManager.userByUsername(username);

            if (user.isPresent()) {
                return new UserDetailsResponse(user.get());
            } else {
                throw new NotFoundException();
            }
        });
    }

    public Route userId(String idParam) {
        return Responder.build(request -> {
            authenticate(request);
            String id = request.params(idParam);
            Optional<User> user = credentialsManager.userById(id);

            if (user.isPresent()) {
                return new UserDetailsResponse(user.get());
            } else {
                throw new NotFoundException();
            }
        });
    }







    /**
     * Check if the request contains a proper authentication token and get the user owning it.
     *
     * @param request   request
     * @return user owning the token
     */
    private User authenticate(Request request) {
        Optional<String> authenticationHeader = Optional.ofNullable(request.headers("Authorization"));

        if (!authenticationHeader.isPresent())
            throw new UnauthorizedException();

        String authorization = authenticationHeader.get();

        if (!authorization.startsWith("Bearer"))
            throw new UnauthorizedException();

        String tokenId = authorization.substring("Bearer".length()).trim();
        Optional<Token> token = authorizer.searchToken(tokenId);

        if (token.isPresent()) {
            return token.get().owner;
        } else {
            throw new UnauthorizedException();
        }
    }

    /** Returns the User (if exists) with username and password passed as arguments */
    /*public User userWithUsernamePassword(String username, String password) {
        for (User user : users()) {
            if (username.compareTo(user.getUsername()) == 0 && password.compareTo(user.getPassword()) == 0) {
                return user;
            }
        }

        return null;
    }*/

    /*
    // Returns the user if exists and if the token is valid with id passed as argument
    public User user(String uuid, String token) throws RestException {
        if (!checkCredentialsUser(uuid, token))
            throw new ForbiddenException();

        return users.get(uuid);
    }
    */

    /**
     *  @param uuid         id of the user to be removed
     *  @param token        authorization token necessary for removing the user
     *
     *  Removes the user passed as argument */
    /*
    public void remove(String uuid, String token) throws RestException {
        if (!checkCredentialsUser(uuid, token))
            throw new ForbiddenException();

        users.remove(uuid);
    }
    */

    /*
    public static int getTokenLifetime() { return tokenLifetime; }

    public void clearUsers() { users.clear(); }
*/
    /**
     *  @param uuid         id of the user
     *  @param token        authorization token necessary for retrieving images
     *
     *  @return  the images of the user specified as argument */
    /*
    public Collection<Image> imagesOfUser(String uuid, String token) {
        if(checkCredentialsUser(uuid,token) && images.get(uuid)!=null && uuid != null) {
            return images.get(uuid).getImages();
        }
        return null;
    }
    */

    /**
     * @param uuid          id of the user
     * @param key           id of the image to be retrieved
     *
     * @return the image in the usare storage of user id which has key id */
    //private Image image(String uuid,String key) {return images.get(uuid).getImage(key);}

    /**
     * @param user          id of the user
     * @param img           image to be added to the storage of user
     * @param token         authorization token necessary for adding an image
     *
     * Add the image to the stotage space of a user
     *
     * @return true if the token is valid, false otherwise */
    /*
    public boolean addImage(String user,Image img, String token) {
        if(checkCredentialsUser(user, token)) {
            String imgID = randomUUID().toString().split("-")[0];
            img.setId(imgID);
            if(images.get(user)==null)
                images.put(user,new UserStorage());
            else
                images.get(user).getStorageSpace().put(imgID, img);
            return true;
        }
        return false;
    }*/

    /**
     * @param usr           user who is asking to login (consider only username and password)
     *
     * @return the user if the credentials (username and password) given as arguments match a user
     *          null otherwise */
    /*
    public String logins(User usr) {
        for (User user : users.values()) {
            if (user.getUsername().compareTo(usr.getUsername()) == 0 &&
                    user.getPassword().compareTo(usr.getPassword()) == 0) {
                return user.addToken(tokenLifetime);
            }
        }

        return null;
    }*/

    /** @return true if the token is registered to the user (as user token) with id uuid and isn't expired yet */
    /*public boolean checkCredentialsUser(String uuid, String token) {
        User user=users.get(uuid);
        return user!=null && user.hasToken(token);
    }*/

    /** @return true if the token is registered to the user (as third party token) with id uuid and isn't expired yet */
    /*public boolean checkThirdPartyCredentials(String uuid, String token) {
        User user=users.get(uuid);
        return user!=null && (user.hasThirdPartyToken(token) || user.hasToken(token));
    }

    public void clearImages() {
        for(int i=0;i < images.size(); i++)
            images.get(i).clearStorage();

        images.clear();
    }*/
}
