package it.polimi.rest.authorization;

import it.polimi.rest.data.DataProvider;
import it.polimi.rest.data.Storage;
import it.polimi.rest.exceptions.ForbiddenException;
import it.polimi.rest.exceptions.UnauthorizedException;
import it.polimi.rest.models.*;
import it.polimi.rest.models.oauth2.OAuth2AccessToken;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.OAuth2ClientsList;

import static it.polimi.rest.exceptions.UnauthorizedException.AuthType.BEARER;

class SecureDataProvider extends DataProvider {

    private final Authorizer authorizer;
    private final SessionManager sessionManager;
    private final Agent agent;

    public SecureDataProvider(Storage storage, SessionManager sessionManager, Authorizer authorizer, Agent agent) {
        super(storage, sessionManager);

        this.authorizer = authorizer;
        this.sessionManager = sessionManager;
        this.agent = agent;
    }

    @Override
    public User userById(User.Id id) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        User user = super.userById(id);

        if (!authorizer.get(id, agent).read) {
            throw new ForbiddenException();
        }

        return user;
    }

    @Override
    public User userByUsername(String username) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        User user = super.userByUsername(username);

        if (!authorizer.get(user.id, agent).read) {
            throw new ForbiddenException();
        }

        return user;
    }

    @Override
    public UsersList users() {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        return super.users();
    }

    @Override
    public void add(User user) {
        super.add(user);

        authorizer.grant(user.id, user.id, Permission.WRITE);
        authorizer.grant(ImagesList.placeholder(user), user.id, Permission.WRITE);
        authorizer.grant(OAuth2ClientsList.placeholder(user), user.id, Permission.WRITE);
    }

    @Override
    public void remove(User.Id id) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        User user = userById(id);

        if (!authorizer.get(user.id, agent).write) {
            throw new ForbiddenException();
        }

        super.remove(id);

        authorizer.remove(id);
    }

    @Override
    public BasicToken basicToken(BasicToken.Id id) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        BasicToken token = super.basicToken(id);

        if (!authorizer.get(token.id, agent).read) {
            throw new ForbiddenException();
        }

        return token;
    }

    @Override
    public void add(BasicToken token) {
        super.add(token);
        authorizer.grant(token.id, token.user, Permission.WRITE);
    }

    @Override
    public void remove(BasicToken.Id id) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        BasicToken token = basicToken(id);

        if (!authorizer.get(token.id, agent).write) {
            throw new ForbiddenException();
        }

        super.remove(id);

        authorizer.remove(id);
    }

    @Override
    public Image image(Image.Id id) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        Image image = super.image(id);

        if (!authorizer.get(image.info.id, agent).read) {
            throw new ForbiddenException();
        }

        return image;
    }

    @Override
    public ImagesList images(String username) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        ImagesList images = super.images(username);

        if (!authorizer.get(images, agent).read) {
            throw new ForbiddenException();
        }

        return images;
    }

    @Override
    public void add(Image image) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        // Check if the agent has write access to the user images list
        if (!authorizer.get(ImagesList.placeholder(image.info.owner), agent).write) {
            throw new ForbiddenException();
        }

        super.add(image);
        authorizer.grant(image.info.id, image.info.owner.id, Permission.WRITE);
    }

    @Override
    public void remove(Image.Id id) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        Image image = image(id);

        if (!authorizer.get(image.info.id, agent).write) {
            throw new ForbiddenException();
        }

        super.remove(id);

        authorizer.remove(id);
    }

    @Override
    public OAuth2Client oAuth2Client(OAuth2Client.Id id) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        OAuth2Client client = super.oAuth2Client(id);

        if (!authorizer.get(client.id, agent).read) {
            throw new ForbiddenException();
        }

        return client;
    }

    @Override
    public OAuth2ClientsList oAuth2Clients(User.Id user) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        OAuth2ClientsList clients = super.oAuth2Clients(user);

        if (!authorizer.get(clients, agent).read) {
            throw new ForbiddenException();
        }

        return clients;
    }

    @Override
    public void add(OAuth2Client client) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        User owner = userById(client.owner.id);

        if (!authorizer.get(OAuth2ClientsList.placeholder(owner), agent).write) {
            throw new ForbiddenException();
        }

        super.add(client);

        authorizer.grant(client.id, owner.id, Permission.WRITE);
        authorizer.grant(client.id, client.id, Permission.READ);
    }

    @Override
    public void remove(OAuth2Client.Id id) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        OAuth2Client client = oAuth2Client(id);

        if (!authorizer.get(client.id, agent).write) {
            throw new ForbiddenException();
        }

        super.remove(id);

        authorizer.remove(id);
    }

    @Override
    public void add(OAuth2AccessToken token) {
        super.add(token);
        token.scope.forEach(scope -> scope.addPermissions(authorizer, sessionManager, token));
    }

    @Override
    public void remove(OAuth2AccessToken.Id id) {
        super.remove(id);
        authorizer.remove(id);
    }

}
