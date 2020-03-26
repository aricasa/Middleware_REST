package it.polimi.rest.models.oauth2;

import com.google.gson.annotations.Expose;
import it.polimi.rest.authorization.SecuredObject;
import it.polimi.rest.models.ImagesList;
import it.polimi.rest.models.Link;
import it.polimi.rest.models.Model;
import it.polimi.rest.models.User;

import java.util.*;

public class OAuth2ClientsList implements Model, SecuredObject {

    public final User owner;
    private final Collection<OAuth2Client> clients;

    @Expose
    public final int count;

    public OAuth2ClientsList(User owner, Collection<OAuth2Client> clients) {
        this.owner = owner;
        this.clients = clients;
        this.count = clients.size();
    }

    public static OAuth2ClientsList placeholder(User user) {
        return new OAuth2ClientsList(user, Collections.emptyList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OAuth2ClientsList that = (OAuth2ClientsList) o;
        return owner.id.equals(that.owner.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner.id);
    }

    @Override
    public Optional<String> self() {
        return owner.oAuth2Clients();
    }

    @Override
    public Map<String, Link> links() {
        Map<String, Link> links = new HashMap<>();
        owner.self().ifPresent(url -> links.put("author", new Link(url)));
        return links;
    }

    @Override
    public Map<String, Object> embedded() {
        return Collections.singletonMap("item", clients);
    }

}
