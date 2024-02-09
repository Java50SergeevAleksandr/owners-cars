package telran.cars.exceptions;

import telran.cars.api.ServiceExceptionMessages;

@SuppressWarnings("serial")
public class IllegalCarsStateException extends IllegalStateException {
	public IllegalCarsStateException() {
		super(ServiceExceptionMessages.CAR_ALREADY_EXISTS);
	}
}