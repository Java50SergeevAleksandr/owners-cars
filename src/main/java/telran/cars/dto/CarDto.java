package telran.cars.dto;

import jakarta.validation.constraints.*;
import static telran.cars.api.ValidationConstants.*;

import java.util.Objects;

public record CarDto(
		@NotEmpty(message = MISSING_CAR_NUMBER_MESSAGE) @Pattern(regexp = CAR_NUMBER_REGEXP, message = WRONG_CAR_NUMBER_MESSAGE) String number,
		@NotEmpty(message = MISSING_CAR_MODEL_MESSAGE) String model) {

	@Override
	public int hashCode() {
		return Objects.hash(number);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CarDto other = (CarDto) obj;
		return Objects.equals(number, other.number);
	}

}
