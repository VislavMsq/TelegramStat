package org.stb.exception;

public class PollExistsException extends RuntimeException {

    public PollExistsException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
    }
}
