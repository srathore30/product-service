package sfa.product_service.exception;

public class BusinessServiceException extends BaseException {

    public BusinessServiceException(int errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
