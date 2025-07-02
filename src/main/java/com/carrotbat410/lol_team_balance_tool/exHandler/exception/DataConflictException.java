package com.carrotbat410.lol_team_balance_tool.exHandler.exception;

public class DataConflictException extends RuntimeException {
    private final String code;

    public DataConflictException() {
        super();
        this.code = null;
    }

    public DataConflictException(String message, String code) {
        super(message);
        this.code = code;
    }

    public DataConflictException(String message) {
        super(message);
        this.code = null;
    }

    public String getCode() {
        return code;
    }
}
