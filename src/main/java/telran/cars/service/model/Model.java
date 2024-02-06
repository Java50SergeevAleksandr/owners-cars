package telran.cars.service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "models")
@Getter
public class Model {
	@EmbeddedId
	ModelYear modelYear;

	@Column(nullable = false)
	String company;

	@Column(name = "engine_power", nullable = false)
	int enginePower;

	@Column(name = "engine_capacity", nullable = false)
	int engineCapacity;
}