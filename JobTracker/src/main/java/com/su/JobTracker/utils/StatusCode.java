package com.su.JobTracker.utils;

public enum StatusCode {
    SUCCESS(200, "Request was successful"),
    OK(201, "Request ok"),
    NO_CONTENT(204, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
	
	LOGGED_OUT(101, "Logged out successful"),
	INVALID_CREDENTIAL(102, "Password does not match"),
	DISABLE_FAILED(109, "Failed to disable"),
	REGISTER_FAILED(900, "Register failed"),
	NOT_LOGIN(901, "No user logged in"),
	USER_EXIST(904, "User already exists"),
	USER_NOT_FOUND(999, "User not found"),
	
	COMPANY_EXIST(804, "Company already exists"),
	UNKNOWN_ERROR(666, "Unknown error"),
	TOO_LONG(555, "Input is too long"),
	
	ALREADY_REVIEWED(701, "Already reviewed"),
	CANNOT_REAPPLY(702, "Cannot reapply now"),
	ALREADY_SAVED(703, "Already saved");
	
	
    private final int code;
    private final String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }
    public String getStringCode() {
    	return Integer.toString(code);
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "StatusCode{" +
               "code=" + code +
               ", message='" + message + '\'' +
               '}';
    }
}
