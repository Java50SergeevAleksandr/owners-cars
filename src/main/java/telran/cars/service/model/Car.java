package telran.cars.service.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import telran.cars.dto.CarDto;
import telran.cars.dto.CarState;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "cars")
public class Car {
	@Id
	@Column(name = "car_number")
	String number;

	@ManyToOne
	@JoinColumns({ @JoinColumn(name = "model_name", nullable = false),
			@JoinColumn(name = "model_year", nullable = false) })
	@Setter
	@OnDelete(action = OnDeleteAction.CASCADE)
	Model model;

	@ManyToOne
	@Setter
	@JoinColumn(name = "owner_id", nullable = true)
	@OnDelete(action = OnDeleteAction.SET_NULL)
	CarOwner carOwner;

	String color;

	@Setter
	Integer kilometers;

	@Enumerated(EnumType.STRING) // value in the table will be a string (by default a number)
	@Column(name = "car_state")
	CarState state;

	private Car(String number, String color, Integer kilometers, CarState state) {

		this.number = number;
		this.color = color;
		this.kilometers = kilometers;
		this.state = state;
	}

	@Override
	public String toString() {
		return "Car [number=" + number + ", color=" + color + ", kilometers=" + kilometers + ", state=" + state + "]";
	}

	public static Car of(CarDto carDto) {
		return new Car(carDto.number(), carDto.color(), carDto.kilometers(), carDto.state());
	}

	public CarDto build() {
		return new CarDto(number, model.modelYear.getName(), model.modelYear.getYear(), color, kilometers, state);
	}
}