package com.gestor_clinica_veterinaria.VeterinaryHospitalManager.Controller;

import com.gestor_clinica_veterinaria.VeterinaryHospitalManager.Dto.ExceptionResponse;
import com.gestor_clinica_veterinaria.VeterinaryHospitalManager.Exceptions.ResourceDuplicatedException;
import com.gestor_clinica_veterinaria.VeterinaryHospitalManager.Exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionController {


  ZoneId zoneId = ZoneId.of("America/Argentina/Buenos_Aires");
  LocalDateTime timestamp = LocalDateTime.now(zoneId);

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ExceptionResponse> handleResourceNotFoundException(
      ResourceNotFoundException resourceNotFoundException,
      HttpServletRequest request) {

    int httpStatus = HttpStatus.NOT_FOUND.value();
    ExceptionResponse exceptionResponse = new ExceptionResponse(
        httpStatus,
        request.getMethod(),
        resourceNotFoundException.getMessage(),
        resourceNotFoundException.getMessage(),
        timestamp,
        null
    );

    return ResponseEntity.status(httpStatus).body(exceptionResponse);
  }

  @ExceptionHandler(ResourceDuplicatedException.class)
  public ResponseEntity<ExceptionResponse> handleResourceDuplicatedException(
      ResourceDuplicatedException resourceDuplicatedException, HttpServletRequest request) {

    int httpStatus = HttpStatus.CONFLICT.value();
    ExceptionResponse exceptionResponse = new ExceptionResponse(
        httpStatus,
        request.getMethod(),
        "Se encontraron datos duplicados",
        resourceDuplicatedException.getMessage(),
        timestamp,
        null
    );

    return ResponseEntity.status(httpStatus).body(exceptionResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException methodArgumentNotValidException, HttpServletRequest request) {

    int httpStatus = HttpStatus.BAD_REQUEST.value();
    List<ObjectError> errors = methodArgumentNotValidException.getAllErrors();
    List<String> details = errors.stream().map(error -> {
      if (error instanceof FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
      }
      return error.getDefaultMessage();
    }).toList();

    ExceptionResponse exceptionResponse = new ExceptionResponse(
        httpStatus,
        request.getMethod(),
        "la request tiene parametros invalidos o incompletos",
        methodArgumentNotValidException.getMessage(),
        timestamp,
        details
    );

    return ResponseEntity.status(httpStatus).body(exceptionResponse);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(
      IllegalArgumentException illegalArgumentException, HttpServletRequest request) {

    int httpStatus = HttpStatus.BAD_REQUEST.value();
    ExceptionResponse apiErrorResponse = new ExceptionResponse(
        httpStatus,
        request.getMethod(),
        illegalArgumentException.getMessage(),
        illegalArgumentException.getMessage(),
        timestamp,
        null
    );

    return ResponseEntity.status(httpStatus).body(apiErrorResponse);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException httpMessageNotReadableException, HttpServletRequest request) {

    int httpStatus = HttpStatus.BAD_REQUEST.value();
    ExceptionResponse apiErrorResponse = new ExceptionResponse(
        httpStatus,
        request.getMethod(),
        "Error en la lectura del HTTP body, Compruebe que el formato es correcto y/o contenga "
            + "data valida.",
        httpMessageNotReadableException.getMessage(),
        timestamp,
        null
    );

    return ResponseEntity.status(httpStatus).body(apiErrorResponse);
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ExceptionResponse> handleHttpMediaTypeNotSupportedException(
      HttpMediaTypeNotSupportedException httpMediaTypeNotSupportedException,
      HttpServletRequest request) {

    int httpStatus = HttpStatus.UNSUPPORTED_MEDIA_TYPE.value();
    ExceptionResponse apiErrorResponse = new ExceptionResponse(
        httpStatus,
        request.getMethod(),
        "Media Type no soportados, los Media Type soportados son: "
            + httpMediaTypeNotSupportedException.getSupportedMediaTypes()
            + " y tu enviaste: " + httpMediaTypeNotSupportedException.getContentType(),
        httpMediaTypeNotSupportedException.getMessage(),
        timestamp,
        null
    );

    return ResponseEntity.status(httpStatus).body(apiErrorResponse);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ExceptionResponse> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException httpRequestMethodNotSupportedException,
      HttpServletRequest request) {

    int httpStatus = HttpStatus.METHOD_NOT_ALLOWED.value();
    ExceptionResponse exceptionResponse = new ExceptionResponse(
        httpStatus,
        request.getMethod(),
        "metodo HTTP no permitado, Revisa el metodo HTTP de la request.",
        httpRequestMethodNotSupportedException.getMessage(),
        timestamp,
        null
    );

    return ResponseEntity.status(httpStatus).body(exceptionResponse);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ExceptionResponse> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException methodArgumentTypeMismatchException,
      HttpServletRequest request) {

    int httpStatus = HttpStatus.BAD_REQUEST.value();
    Object valueRejected = methodArgumentTypeMismatchException.getValue();
    String propertyName = methodArgumentTypeMismatchException.getName();

    ExceptionResponse exceptionResponse = new ExceptionResponse(
        httpStatus,
        request.getMethod(),
        "Request Invalido: el valor proporcionado " + valueRejected
            + " no tiene el type esperado " + "para el " + propertyName,
        methodArgumentTypeMismatchException.getMessage(),
        timestamp,
        null
    );

    return ResponseEntity.status(httpStatus).body(exceptionResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionResponse> handleAllException(Exception exception,
      HttpServletRequest request) {

    int httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
    ExceptionResponse exceptionResponse = new ExceptionResponse(
        httpStatus,
        request.getMethod(),
        "Ocurrio un error Inesperado",
        exception.getMessage(),
        timestamp,
        null
    );

    return ResponseEntity.status(httpStatus).body(exceptionResponse);
  }


}
