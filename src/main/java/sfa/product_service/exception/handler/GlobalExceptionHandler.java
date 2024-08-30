package sfa.product_service.exception.handler;


import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import sfa.product_service.constant.ApiErrorCodes;
import sfa.product_service.dto.response.ErrorResponse;
import sfa.product_service.exception.*;

import java.util.Objects;

@RestController
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    public static final String TRACE = "trace";

    @Value("${application.config.trace}")
    private boolean printStackTrace;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ApiErrorCodes.INVALID_INPUT.getErrorCode(), ApiErrorCodes.INVALID_INPUT.getErrorMessage());
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.unprocessableEntity().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception exception, WebRequest request) {
        log.error("Unknown error occurred", exception);
        return buildErrorResponse(exception, "Unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception exception, HttpStatus httpStatus, WebRequest request) {
        return buildErrorResponse(exception, exception.getMessage(), httpStatus, request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception exception, String message, HttpStatus httpStatus, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message);
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    private ResponseEntity<ErrorResponse> buildCustomErrorResponse(BaseException exception, HttpStatus httpStatus, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getErrorCode(), exception.getMessage());
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    private boolean isTraceOn(WebRequest request) {
        String[] value = request.getParameterValues(TRACE);
        return Objects.nonNull(value) && value.length > 0 && value[0].contentEquals("true");
    }

    @ExceptionHandler(NoSuchElementFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NoSuchElementFoundException exception, WebRequest request) {
        log.error("Failed to find the requested element", exception);
        return buildCustomErrorResponse(exception, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(BusinessServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleServiceFoundException(BusinessServiceException businessServiceException, WebRequest request) {
        log.error("Business Exception Occurred ", businessServiceException);
        return buildCustomErrorResponse(businessServiceException, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleNotFoundException(ValidationException exception, WebRequest request) {
        log.error("Validation failed ", exception);
        return buildCustomErrorResponse(exception, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(InvalidInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidInputException(InvalidInputException exception, WebRequest request) {
        log.error("Invalid input error ", exception);
        return buildCustomErrorResponse(exception, HttpStatus.BAD_REQUEST, request);
    }

}


