package telran.cars.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import telran.cars.dto.*;
import telran.cars.service.model.*;

@Slf4j
@Service
public class CarsServiceImpl implements CarsService {
	HashMap<Long, CarOwner> owners = new HashMap<>();
	HashMap<String, Car> cars = new HashMap<>();

	@Override
	public PersonDto addPerson(PersonDto personDto) {
		log.debug("add person with id: {}", personDto.id());
		CarOwner owner = new CarOwner(personDto);
		CarOwner res = owners.putIfAbsent(personDto.id(), owner);
		if (res != null) {
			throw new IllegalStateException("person with id" + personDto.id() + " already exist");
		}
		return owner.build();

	}

	@Override
	public CarDto addCar(CarDto carDto) {
		Car car = new Car(carDto);
		Car res = cars.putIfAbsent(carDto.number(), car);
		if (res != null) {
			throw new IllegalStateException();
		}
		return car.build();
	}

	@Override
	public PersonDto updatePerson(PersonDto personDto) {
		CarOwner res = owners.computeIfPresent(personDto.id(), (id, dto) -> new CarOwner(personDto));
		if (res == null) {
			throw new IllegalStateException();
		}
		return res.build();
	}

	@Override
	public PersonDto deletePerson(long id) {
		CarOwner res = owners.remove(id);
		if (res == null) {
			throw new IllegalStateException();
		}
		return res.build();
	}

	@Override
	public CarDto deleteCar(String carNumber) {
		Car res = cars.remove(carNumber);
		if (res == null) {
			throw new IllegalStateException();
		}
		return res.build();
	}

	@Override
	public TradeDealDto purchase(TradeDealDto tradeDeal) {
		Car car = cars.get(tradeDeal.carNumber());
		if (car == null) {
			throw new IllegalStateException();
		}
		CarOwner owner = owners.get(tradeDeal.personId());
		if (owner != null) {
			owner.getCars().add(car);
		}
		car.setOwner(owner);

		return tradeDeal;
	}

	@Override
	public List<CarDto> getOwnerCars(long id) {
		CarOwner res = owners.get(id);
		if (res == null) {
			throw new IllegalStateException();
		}
		List<CarDto> list = res.getCars().stream().map(c -> c.build()).collect(Collectors.toList());
		return list;
	}

	@Override
	public PersonDto getCarOwner(String carNumber) {
		Car res = cars.get(carNumber);
		if (res == null) {
			throw new IllegalStateException();
		}
		return res.getOwner().build();
	}

	@Override
	public void clearAll() {
		owners.clear();
		cars.clear();
	}
}