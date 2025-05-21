package com.holiday.system.exception;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.holiday.system.dto.ApiErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<String> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
		Throwable cause = ex.getCause();
		if (cause instanceof InvalidFormatException) {

			InvalidFormatException ife = (InvalidFormatException) cause;
			if (ife.getTargetType().equals(LocalDate.class)) {
				List<Reference> path = ife.getPath();
				String fieldName = !path.isEmpty() ? path.get(path.size() - 1).getFieldName() : "Unknown field";
				String targetType = ife.getTargetType().getSimpleName();
				String value = String.valueOf(ife.getValue());
				String errorMessage = String.format("Invalid value '%s' for field '%s'. Expected type: %s.", value,
						fieldName, targetType);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(errorMessage + " Please use the format yyyy-MM-dd.");
			}
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request body is missing or malformed.");

	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {

		Map<String, String> errors = new HashMap<>();

		ex.getBindingResult().getFieldErrors().forEach((FieldError error) -> {
			String fieldName = error.getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<String> handleMissingParams(MissingServletRequestParameterException ex,
			HttpServletRequest request) {
		return new ResponseEntity<>("Required parameter '" + ex.getParameterName() + "' is not present.",
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
		String paramName = ex.getName();
		String message = String.format("Invalid Type value for parameter '%s': %s", paramName, ex.getValue());
		return ResponseEntity.badRequest().body(message);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<String> handleBadRequest(BadRequestException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MandatoryFieldException.class)
	public ResponseEntity<ApiErrorResponse> handleMandatoryFieldException(MandatoryFieldException ex,
			HttpServletRequest request) {
		ApiErrorResponse error = new ApiErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
				"Mandatory Field Missing", ex.getMessage(), request.getRequestURI());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

}
