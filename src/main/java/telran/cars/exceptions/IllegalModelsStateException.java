package telran.cars.exceptions;

import telran.cars.api.ServiceExceptionMessages;

@SuppressWarnings("serial")
public class IllegalModelsStateException extends IllegalStateException {
public IllegalModelsStateException() {
	super(ServiceExceptionMessages.MODEL_ALREADY_EXISTS);
}
}
