package it.polimi.rest.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.*;

public abstract class Response {

    protected static final String APPLICATION_JSON = "application/json";

    public final int code;
    public final String type;
    public final Object payload;

    private final Collection<Link> links_ = new ArrayList<>();
    private final Map<String, Object> embedded_ = new HashMap<>();

    public Response(int code, String type, Object payload) {
        this.code = code;
        this.type = type;
        this.payload = payload;
    }

    public abstract Optional<String> self();

    protected final void link(Link link) {
        links_.add(link);
    }

    public final Collection<Link> links() {
        return Collections.unmodifiableCollection(links_);
    }

    protected final void embed(String name, Object object) {
        embedded_.put(name, object);
    }

    public final Map<String, Object> embedded() {
        return Collections.unmodifiableMap(embedded_);
    }

}
