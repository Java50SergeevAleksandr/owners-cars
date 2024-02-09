package telran.cars.exceptions;

import telran.cars.api.ServiceExceptionMessages;

@SuppressWarnings("serial")
public class PersonNotFoundException extends NotFoundException {

	public PersonNotFoundException() {
		super(ServiceExceptionMessages.PERSON_NOT_FOUND);
	}

}