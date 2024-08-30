package sfa.product_service.exception;

public abstract class BaseException extends RuntimeException{
    protected final int errorCode;
    protected final String errorMessage;

    protected BaseException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public abstract int getErrorCode();

    public abstract String getErrorMessage();


}
