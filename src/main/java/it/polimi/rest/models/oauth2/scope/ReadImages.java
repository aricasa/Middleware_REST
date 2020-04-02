package it.polimi.rest.models.oauth2.scope;

import it.polimi.rest.authorization.*;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.models.ImagesList;
import it.polimi.rest.models.User;

class ReadImages extends Scope {

    public ReadImages() {
        super(Scope.READ_IMAGES);
    }

    @Override
    protected void addPermissions(Authorizer authorizer, SessionManager sessionManager, User.Id user, Agent agent) {
        DataProvider dataProvider = sessionManager.dataProvider(user);

        User u = dataProvider.userById(user);

        ImagesList images = dataProvider.images(u.username);
        authorizer.grant(images, agent, Permission.READ);
        images.forEach(image -> authorizer.grant(image.id, agent, Permission.READ));
    }

}
