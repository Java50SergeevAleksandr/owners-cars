package telran.cars.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.cars.dto.*;
import telran.cars.exceptions.*;
import telran.cars.repo.*;
import telran.cars.service.model.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarsServiceImpl implements CarsService {
	final CarRepo carRepo;
	final CarOwnerRepo carOwnerRepo;
	final ModelRepo modelRepo;
	final TradeDealRepo tradeDealRepo;

	@Override
	@Transactional
	public PersonDto addPerson(PersonDto personDto) {
		if (carOwnerRepo.existsById(personDto.id())) {
			throw new IllegalPersonsStateException();
		}
		CarOwner carOwner = CarOwner.of(personDto);
		carOwnerRepo.save(carOwner);
		log.debug("person {} has been saved", personDto);
		return personDto;
	}

	@Override
	@Transactional
	public CarDto addCar(CarDto carDto) {
		if (carRepo.existsById(carDto.number())) {
			throw new IllegalCarsStateException();
		}
		Model model = modelRepo.findById(new ModelYear(carDto.model(), carDto.year()))
				.orElseThrow(() -> new ModelNotFoundException());
		Car car = Car.of(carDto);
		car.setModel(model);
		carRepo.save(car);
		log.debug("car {} has been saved", carDto);
		return carDto;
	}

	@Override
	public ModelDto addModel(ModelDto modelDto) {
		if (modelRepo.existsById(new ModelYear(modelDto.modelName(), modelDto.modelYear()))) {
			throw new IllegalModelsStateException();
		}
		Model model = Model.of(modelDto);
		modelRepo.save(model);
		log.debug("model {} has been saved", modelDto);
		return modelDto;
	}

	@Override
	@Transactional
	public PersonDto updatePerson(PersonDto personDto) {
		CarOwner carOwner = carOwnerRepo.findById(personDto.id()).orElseThrow(() -> new PersonNotFoundException());
		carOwner.setEmail(personDto.email());
		return personDto;
	}

	@Override
	@Transactional
	public PersonDto deletePerson(long id) {
		CarOwner carOwner = carOwnerRepo.findById(id).orElseThrow(() -> new PersonNotFoundException());
		carOwnerRepo.deleteById(id);
		return carOwner.build();
	}

	@Override
	@Transactional
	public CarDto deleteCar(String carNumber) {
		Car car = carRepo.findById(carNumber).orElseThrow(() -> new CarNotFoundException());
		carRepo.deleteById(carNumber);
		return car.build();
	}

	@Override
	@Transactional
	public TradeDealDto purchase(TradeDealDto tradeDealDto) {
		Car car = carRepo.findById(tradeDealDto.carNumber()).orElseThrow(() -> new CarNotFoundException());
		CarOwner oldCarOwner = car.getCarOwner();
		CarOwner newCarOwner = null;
		Long personId = tradeDealDto.personId();
		if (personId != null) {
			log.debug("ID of new car's owner is {}", personId);
			newCarOwner = carOwnerRepo.findById(personId).orElseThrow(() -> new PersonNotFoundException());
			if (oldCarOwner != null && oldCarOwner.getId() == personId) {
				throw new TradeDealIllegalStateException();
			}
		} else if (oldCarOwner == null) {
			throw new TradeDealIllegalStateException();
		}
		TradeDeal tradeDeal = new TradeDeal();
		tradeDeal.setCar(car);
		tradeDeal.setCarOwner(newCarOwner);
		tradeDeal.setDate(LocalDate.parse(tradeDealDto.date()));
		car.setCarOwner(newCarOwner);
		tradeDealRepo.save(tradeDeal);
		log.debug("trade: {} has been saved", tradeDealDto);
		return tradeDealDto;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CarDto> getOwnerCars(long id) {
		List<Car> cars = carRepo.findByCarOwnerId(id);
		if (cars.isEmpty()) {
			log.warn("person with id {} has no cars", id);
		} else {
			log.debug("person with id {} has {} cars {}", id, cars.size());
		}
		return cars.stream().map(Car::build).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public PersonDto getCarOwner(String carNumber) {
		Car car = carRepo.findById(carNumber).orElseThrow(() -> new CarNotFoundException());
		CarOwner carOwner = car.getCarOwner();
		return carOwner != null ? carOwner.build() : null;
	}

	@Override
	public List<String> mostSoldModelNames() {
		List<String> res = modelRepo.findMostSoldModelNames();
		log.trace("most sold model names are {}", res);

		return res;
	}

	@Override
	public List<ModelNameAmount> mostPopularModelNames(int nModels) {
		List<ModelNameAmount> res = modelRepo.findMostPopularModelNames(nModels);
		res.forEach(mn -> log.debug("model name is {}, number of cars {}", mn.getName(), mn.getAmount()));
		return res;
	}

	@Override
	/**
	 * returns count of trade deals for a given 'modelName' at a given year / month
	 * Try to apply only interface method name without @Query annotation
	 */
	public long countTradeDealAtMonthModel(String modelName, int month, int year) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	/**
	 * returns list of a given number of most popular (most cars amount) model names
	 * and appropriate amounts of the cars, owners of which have an age in a given
	 * range
	 */
	public List<ModelNameAmount> mostPopularModelNameByOwnerAges(int nModels, int ageFrom, int ageTo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	/**
	 * returns one most popular color of a given model
	 */
	public String oneMostPopularColorModel(String model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	/**
	 * returns minimal values of engine power and capacity of car owners having an
	 * age in a given range
	 */
	public EnginePowerCapacity minEnginePowerCapacityByOwnerAges(int ageFrom, int ageTo) {
		// TODO Auto-generated method stub
		return null;
	}
}