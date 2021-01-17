package org.example.cab_management_portal.exceptions;

public class BaseException extends Exception {

    private static final long serialVersionUID = 8538048908000861388L;

    private String message = null;

    public BaseException(String message) {
        super(message);
        this.message = message;
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        return message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
