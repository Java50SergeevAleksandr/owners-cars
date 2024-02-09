package telran.cars.exceptions;

import telran.cars.api.ServiceExceptionMessages;

@SuppressWarnings("serial")
public class CarNotFoundException extends NotFoundException {

	public CarNotFoundException() {
		super(ServiceExceptionMessages.CAR_NOT_FOUND);

	}

}
