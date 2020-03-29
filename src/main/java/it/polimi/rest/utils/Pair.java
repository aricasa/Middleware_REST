package it.polimi.rest.utils;

public class Pair<X, Y> {

    public final X first;
    public final Y second;

    /**
     * Constructor.
     *
     * @param first     first element
     * @param second    second element
     */
    public Pair(X first, Y second) {
        this.first = first;
        this.second = second;
    }

}
