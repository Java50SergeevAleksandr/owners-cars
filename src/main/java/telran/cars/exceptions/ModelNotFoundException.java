package telran.cars.exceptions;

import telran.cars.api.ServiceExceptionMessages;

@SuppressWarnings("serial")
public class ModelNotFoundException extends NotFoundException {

	public ModelNotFoundException() {
		super(ServiceExceptionMessages.MODEL_NOT_FOUND);
	}

}