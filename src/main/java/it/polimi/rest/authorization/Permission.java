package it.polimi.rest.authorization;

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

}
