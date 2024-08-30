package sfa.product_service.exception;

import lombok.Getter;

@Getter
public class NoSuchElementFoundException extends BaseException {

    public NoSuchElementFoundException(int errorCode, String errorMessage) {
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
