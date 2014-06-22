
package me.heldplayer.chat.framework.auth;

public class AuthenticationException extends Exception {

    private static final long serialVersionUID = -3255867925255549831L;

    public AuthenticationException() {
        super();
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(Throwable cause) {
        super(cause);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

}
