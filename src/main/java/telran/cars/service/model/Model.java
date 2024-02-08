package telran.cars.service.model;

import jakarta.persistence.*;
import lombok.*;
import telran.cars.dto.ModelDto;

@Entity
@Table(name = "models")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class Model {
	@EmbeddedId
	ModelYear modelYear;

	@Column(nullable = false)
	String company;

	@Column(name = "engine_power", nullable = false)
	Integer enginePower;

	@Column(name = "engine_capacity", nullable = false)
	Integer engineCapacity;

	public static Model of(ModelDto modelDto) {
		ModelYear modelYear = new ModelYear(modelDto.modelName(), modelDto.modelYear());
		return new Model(modelYear, modelDto.company(), modelDto.enginePower(), modelDto.engineCapacity());
	}

	public ModelDto build() {
		return new ModelDto(modelYear.getName(), modelYear.getYear(), company, enginePower, engineCapacity);
	}
}