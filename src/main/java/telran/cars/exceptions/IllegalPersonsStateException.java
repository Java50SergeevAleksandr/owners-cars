package telran.cars.exceptions;

import telran.cars.api.ServiceExceptionMessages;

@SuppressWarnings("serial")
public class IllegalPersonsStateException extends IllegalStateException {
	public IllegalPersonsStateException() {
		super(ServiceExceptionMessages.PERSON_ALREADY_EXISTS);
	}
}