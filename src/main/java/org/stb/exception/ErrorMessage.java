package org.stb.exception;

import lombok.Getter;

@Getter
public enum ErrorMessage {
    POLL_EXISTS("Poll with this text already exists");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }
}
