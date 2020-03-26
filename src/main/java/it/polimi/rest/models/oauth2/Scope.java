package it.polimi.rest.models.oauth2;

import it.polimi.rest.exceptions.BadRequestException;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class Scope {

    public static final String READ_USER = "read_user";
    public static final String READ_IMAGES = "read_images";

    public final String scope;

    public Scope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return scope;
    }

}
