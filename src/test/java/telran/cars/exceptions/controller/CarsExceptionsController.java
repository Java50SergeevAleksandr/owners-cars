package telran.cars.exceptions.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import telran.cars.exceptions.NotFoundException;

@ControllerAdvice
public class CarsExceptionsController {
	@ExceptionHandler(NotFoundException.class)
	ResponseEntity<String> notFoundHandler(NotFoundException e) {
		return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
	}

//TODO Exception handler for IllegalStateException returning response with status BAD_REQUEST (code 400) and body containing error message
	@ExceptionHandler(IllegalStateException.class)
	ResponseEntity<String> illegalStateHandler(IllegalStateException e) {
		return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}
}
