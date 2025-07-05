package com.carrotbat410.lol_team_balance_tool.exHandler.exception;

public class RiotApiNotFoundException extends RuntimeException {
    private final String code;

    public RiotApiNotFoundException() {
        super();
        this.code = null;
    }

    public RiotApiNotFoundException(String message, String code) {
        super(message);
        this.code = code;
    }

    public RiotApiNotFoundException(String message) {
        super(message);
        this.code = null;
    }

    public String getCode() {
        return code;
    }
}
