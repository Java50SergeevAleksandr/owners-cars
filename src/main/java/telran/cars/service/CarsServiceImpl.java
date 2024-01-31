package telran.cars.service;

import java.util.*;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import telran.cars.dto.*;
import telran.cars.exceptions.NotFoundException;
import telran.cars.service.model.*;

@Service("carsService")
@Scope("prototype")
@Slf4j
public class CarsServiceImpl implements CarsService {
	HashMap<Long, CarOwner> owners = new HashMap<>();
	HashMap<String, Car> cars = new HashMap<>();

	@Override
	public PersonDto addPerson(PersonDto personDto) {
		log.debug("add person: received personDto {}", personDto);
		long id = personDto.id();
		CarOwner res = owners.putIfAbsent(id, new CarOwner(personDto));
		if (res != null) {
			throw new IllegalStateException(String.format("person %d already exists", id));
		}
		return personDto;

	}

	@Override
	public CarDto addCar(CarDto carDto) {
		log.debug("add car: received carDto {}", carDto);
		String carNumber = carDto.number();
		Car res = cars.putIfAbsent(carNumber, new Car(carDto));
		if (res != null) {
			throw new IllegalStateException(String.format("car %s already exists", carNumber));
		}
		return carDto;
	}

	@Override
	public PersonDto updatePerson(PersonDto personDto) {
		log.debug("update person: received personDto {}", personDto);
		long id = personDto.id();
		CarOwner res = owners.computeIfPresent(id, (k, co) -> {
			co.setEmail(personDto.email());
			return co;
		});
		if (res == null) {
			throw new NotFoundException(String.format("person %d doesn't exists", id));
		}
		return res.build();
	}

	@Override
	public PersonDto deletePerson(long id) {
		log.debug("delete person: received id {}", id);
		CarOwner person = owners.remove(id);
		if (person == null) {
			throw new NotFoundException(String.format("person %d doesn't exists", id));
		}
		List<Car> cars = person.getCars();
		cars.forEach(c -> c.setOwner(null));
		return person.build();
	}

	@Override
	public CarDto deleteCar(String carNumber) {
		log.debug("delete car: received car number {}", carNumber);
		Car car = cars.remove(carNumber);
		if (car == null) {
			throw new NotFoundException(String.format("car %s doesn't exists", carNumber));
		}
		CarOwner owner = car.getOwner();
		
		if (owner != null) {
			owner.getCars().remove(car);
		}

		return car.build();
	}

	@Override
	public TradeDealDto purchase(TradeDealDto tradeDeal) {
		log.debug("purchase: received car {}, owner {}", tradeDeal.carNumber(), tradeDeal.personId());
		String carNumber = tradeDeal.carNumber();
		Car car = cars.get(tradeDeal.carNumber());
		CarOwner owner = null;
		if (car == null) {
			throw new NotFoundException(String.format("car %s doesn't exists", carNumber));
		}
		Long personId = tradeDeal.personId();
		CarOwner oldOwner = car.getOwner();
		checkSameOwner(personId, oldOwner);
		if (personId != null) {
			log.debug("new owner exists");
			owner = owners.get(personId);
			if (owner == null) {
				throw new NotFoundException(String.format("person %d doesn't exists", personId));
			}
			owner.getCars().add(car);
		}

		if (oldOwner != null) {
			oldOwner.getCars().remove(car);
		}

		car.setOwner(owner);
		return tradeDeal;
	}

	private void checkSameOwner(Long personId, CarOwner oldOwner) {
		if ((oldOwner == null && personId == null) || (oldOwner != null && personId == oldOwner.getId())) {
			throw new IllegalStateException("trade deal with same owner");
		}

	}

	@Override
	public List<CarDto> getOwnerCars(long id) {
		log.debug("get  owner cars: received id {}", id);
		CarOwner res = owners.get(id);
		if (res == null) {
			throw new NotFoundException(String.format("person %d doesn't exists", id));
		}
		return res.getCars().stream().map(Car::build).toList();

	}

	@Override
	public PersonDto getCarOwner(String carNumber) {
		log.debug("get car owner: received car number {}", carNumber);
		Car car = cars.get(carNumber);
		if (car == null) {
			throw new NotFoundException(String.format("car %s doesn't exists", carNumber));
		}
		CarOwner owner = car.getOwner();
		return owner == null ? null : owner.build();
	}

}