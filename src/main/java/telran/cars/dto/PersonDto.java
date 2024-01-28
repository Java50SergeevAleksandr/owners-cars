package telran.cars.dto;

import jakarta.validation.constraints.*;
import static telran.cars.api.ValidationConstants.*;

import java.util.Objects;

public record PersonDto(
		@NotNull(message = MISSING_PERSON_ID_MESSAGE) @Min(value = MIN_PERSON_ID_VALUE, message = WRONG_MIN_PERSON_ID_VALUE) @Max(value = MAX_PERSON_ID_VALUE, message = WRONG_MAX_PERSON_ID_VALUE) Long id,
		@NotEmpty(message = MISSING_PERSON_NAME_MESSAGE) String name,
		@NotEmpty(message = MISSING_BIRTH_DATE_MESSAGE) @Pattern(regexp = BIRTH_DATE_REGEXP, message = WRONG_DATE_FORMAT) String birthDate,
		@NotEmpty(message = MISSING_PERSON_EMAIL) @Email(message = WRONG_EMAIL_FORMAT) String email) {

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersonDto other = (PersonDto) obj;
		return Objects.equals(id, other.id);
	}

}