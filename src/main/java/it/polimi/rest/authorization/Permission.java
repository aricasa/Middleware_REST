package it.polimi.rest.authorization;

import java.util.ArrayList;
import java.util.Collection;

public enum Permission {

    NONE(false, false),
    READ(true, false),
    WRITE(true, true);

    public final boolean read;
    public final boolean write;

    Permission(boolean read, boolean write) {
        this.read = read;
        this.write = write;
    }

    @Override
    public String toString() {
        Collection<String> permissions = new ArrayList<>();

        if (read) {
            permissions.add("READ");
        }

        if (write) {
            permissions.add("WRITE");
        }

        if (permissions.isEmpty()) {
            return "NONE";
        } else {
            return String.join(" + ", permissions);
        }
    }

}
