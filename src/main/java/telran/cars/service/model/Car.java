package telran.cars.service.model;

import lombok.Getter;
import telran.cars.dto.CarDto;

@Getter
public class Car {
	String number;
	String model;
	CarOwner owner;

	public Car(CarDto carDto) {
		number = carDto.number();
		model = carDto.model();
	}

	public CarDto build() {
		return new CarDto(number, model);
	}

	public void setOwner(CarOwner owner) {
		this.owner = owner;
	}
}