package sfa.product_service.constant;

import lombok.Getter;

@Getter
public enum ApiErrorCodes implements Error {
    INVALID_INPUT(1001, "Invalid request input"),
    NOT_FOUND(1002, "Resource not found"),
    INVALID_SEARCH_CRITERIA(1003, "Invalid search criteria"),
    PRODUCT_NOT_FOUND(1004, "Product not found"),
    PRODUCT__PRICE_NOT_FOUND(1005, "Product price not found");

    private int errorCode;
    private String errorMessage;

    ApiErrorCodes(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
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

