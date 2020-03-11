package it.polimi.rest;

public class Logger {

    private final String tag;

    /**
     * Constructor.
     *
     * @param clazz     class whose name has to be used as tag
     */
    public Logger(Class<?> clazz) {
        this.tag = clazz.getSimpleName();
    }

    /**
     * Constructor.
     *
     * @param tag   tag to be displayed before each log message
     */
    public Logger(String tag) {
        this.tag = tag;
    }

    /**
     * Log debug message to stdout.
     *
     * @param message   message to be shown
     */
    public void d(String message) {
        synchronized (System.out) {
            System.out.println("[" + tag + "] " + message);
        }
    }

    /**
     * Log warning message to stderr.
     *
     * @param message   message to be shown
     */
    public void w(String message) {
        synchronized (System.out) {
            System.err.println("[" + tag + "] " + message);
        }
    }

    /**
     * Log error message to stderr.
     *
     * @param message   message to be shown
     */
    public void e(String message) {
        synchronized (System.out) {
            System.err.println("[" + tag + "] " + message);
        }
    }

}