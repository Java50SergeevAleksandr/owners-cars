package telran.cars.dto;

import jakarta.validation.constraints.*;
import static telran.cars.api.ValidationConstants.*;

import java.util.Objects;

public record ModelDto(@NotEmpty(message = MISSING_MODEL_NAME_MESSAGE) String modelName,
		@NotEmpty(message = MISSING_MODEL_YEAR_MESSAGE) @Min(value = MIN_MODEL_YEAR, message = WRONG_MIN_YEAR) Integer modelYear,
		@NotEmpty(message = MISSING_COMPANY_MESSAGE) String company, Integer enginePower, Integer engineCapacity) {

	@Override
	public int hashCode() {
		return Objects.hash(modelName, modelYear);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModelDto other = (ModelDto) obj;
		return Objects.equals(modelName, other.modelName) && Objects.equals(modelYear, other.modelYear);
	}

}
