package telran.cars.exceptions;

import telran.cars.api.ServiceExceptionMessages;

@SuppressWarnings("serial")
public class TradeDealIllegalStateException extends IllegalStateException {
	public TradeDealIllegalStateException() {
		super(ServiceExceptionMessages.SAME_CAR_OWNER);
	}
}