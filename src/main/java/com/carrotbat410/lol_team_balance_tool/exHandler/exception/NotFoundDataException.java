package com.carrotbat410.lol_team_balance_tool.exHandler.exception;

public class NotFoundDataException extends RuntimeException {
    private final String code;

    public NotFoundDataException() {
        super();
        this.code = null;
    }

    public NotFoundDataException(String message, String code) {
        super(message);
        this.code = code;
    }

    public NotFoundDataException(String message) {
        super(message);
        this.code = null;
    }

    public String getCode() {
        return code;
    }
}
