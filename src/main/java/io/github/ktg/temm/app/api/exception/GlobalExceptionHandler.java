package io.github.ktg.temm.app.api.exception;

import static io.github.ktg.temm.app.api.exception.GlobalErrorCode.INTERNAL_SERVER_ERROR;
import static io.github.ktg.temm.app.api.exception.GlobalErrorCode.INVALID_INPUT;

import io.github.ktg.temm.domain.exception.BusinessException;
import io.github.ktg.temm.domain.exception.ErrorCode;
import io.github.ktg.temm.domain.exception.ErrorType;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        HttpStatus httpStatus = mappingHttpStatusByErrorCode(errorCode);
        return ResponseEntity
            .status(httpStatus)
            .body(new ErrorResponse(errorCode.name(), e.getMessage()));
    }

    private HttpStatus mappingHttpStatusByErrorCode(ErrorCode errorCode) {
        ErrorType errorType = errorCode.getErrorType();
        return switch (errorType) {
            case INVALID_INPUT -> HttpStatus.BAD_REQUEST;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case ENTITY_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            case BUSINESS_RULE_VIOLATION -> HttpStatus.CONFLICT;
        };
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        String errorMessage = fieldErrors.stream()
            .map(fieldError -> String.format("%s (%s)", fieldError.getDefaultMessage(), fieldError.getField()))
            .collect(Collectors.joining(", "));

        return ResponseEntity
            .badRequest()
            .body(new ErrorResponse(INVALID_INPUT.name(), errorMessage));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        Class<?> requiredType = ex.getRequiredType();
        String requiredTypeName = requiredType == null ? "" : requiredType.getSimpleName();
        String errorMessage = String.format("'%s' is not type '%s'", ex.getValue(), requiredTypeName);
        return ResponseEntity
            .badRequest()
            .body(new ErrorResponse(INVALID_INPUT.name(), errorMessage));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {

        String errorMessage = ex.getAllErrors().stream()
            .map(MessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.joining(", "));

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(INVALID_INPUT.name(), errorMessage));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.warn("unhandled exception: {}", e.getMessage());
        return ResponseEntity.internalServerError().body(
            new ErrorResponse(
                INTERNAL_SERVER_ERROR.name(), INTERNAL_SERVER_ERROR.getMessage())
        );
    }

}
