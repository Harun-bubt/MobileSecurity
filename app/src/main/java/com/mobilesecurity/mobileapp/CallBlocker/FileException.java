package com.mobilesecurity.mobileapp.CallBlocker;



@SuppressWarnings("SameParameterValue")
public class FileException extends Exception {
    public static final int MEDIA_ERROR = 0;
    public static final int READ_FILE_ERROR = 1;
    public static final int WRITE_FILE_ERROR = 2;
    private final int errorCode;

    public FileException(int errorCode) {
        this.errorCode = errorCode;
    }

    public FileException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public FileException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
