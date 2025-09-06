package com.carrotbat410.lol_team_balance_tool.exHandler.exception;

public class UnprocessableContentException extends RuntimeException {
    private String code;

    public UnprocessableContentException(String message) {
        super(message);
        this.code = "UNPROCESSABLE_CONTENT";
    }

    public UnprocessableContentException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
