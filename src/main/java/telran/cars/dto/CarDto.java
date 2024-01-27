package telran.cars.dto;

import jakarta.validation.constraints.*;
import static telran.cars.api.ValidationConstants.*;

public record CarDto(
		@NotEmpty(message = MISSING_CAR_NUMBER_MESSAGE) @Pattern(regexp = CAR_NUMBER_REGEXP, message = WRONG_CAR_NUMBER_MESSAGE) String number,
		@NotEmpty(message = MISSING_CAR_MODEL_MESSAGE) String model) {

}
