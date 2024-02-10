package telran.cars;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.jdbc.Sql;
import telran.cars.repo.*;
import telran.cars.dto.*;
import telran.cars.exceptions.*;

import telran.cars.service.CarsService;
import telran.cars.service.model.*;

@SpringBootTest
//FIXME accordingly to SQL script
@Sql(scripts = { "classpath:test_data.sql" })
class CarsServiceTest {
	private static final String MODEL1 = "model1";
	private static final String MODEL2 = "model2";
	private static final String MODEL3 = "model3";
	private static final String MODEL4 = "model4";
	private static final String CAR_NUMBER_1 = "111-11-111";
	private static final String CAR_NUMBER_2 = "222-11-111";
	private static final String CAR_NUMBER_3 = "333-11-111";
	private static final String CAR_NUMBER_4 = "444-44-444";
	private static final String CAR_NUMBER_5 = "555-55-555";
	private static final Long PERSON_ID_1 = 123l;
	private static final String NAME1 = "name1";
	private static final String BIRTH_DATE_1 = "2000-10-10";
	private static final String EMAIL1 = "name1@gmail.com";
	private static final String NEW_EMAIL = "name1@tel-ran.co.il";
	private static final Long PERSON_ID_2 = 124l;
	private static final String NAME2 = "name2";
	private static final String BIRTH_DATE_2 = "2000-10-10";
	private static final String EMAIL2 = "name2@gmail.com";
	private static final Long PERSON_ID_NOT_EXISTS = 1111111111L;
	private static final String DATE_TRADE_DEAL_1 = "2024-01-01";

	CarDto car1 = new CarDto(CAR_NUMBER_1, MODEL1, 2020, "red", 1000, CarState.GOOD);
	CarDto car2 = new CarDto(CAR_NUMBER_2, MODEL1, 2020, "silver", 10000, CarState.OLD);
	CarDto car3 = new CarDto(CAR_NUMBER_3, MODEL4, 2023, "white", 0, CarState.NEW);
	CarDto car4 = new CarDto(CAR_NUMBER_4, MODEL4, 2023, "black", 0, CarState.NEW);
	CarDto car5 = new CarDto(CAR_NUMBER_5, MODEL3, 2021, "silver", 5000, CarState.MIDDLE);
	PersonDto personDto = new PersonDto(PERSON_ID_NOT_EXISTS, NAME1, BIRTH_DATE_1, EMAIL1);
	PersonDto personDto1 = new PersonDto(PERSON_ID_1, NAME1, BIRTH_DATE_1, EMAIL1);
	PersonDto personDto2 = new PersonDto(PERSON_ID_2, NAME2, BIRTH_DATE_2, EMAIL2);

	@Autowired
	CarOwnerRepo carOwnerRepo;

	@Autowired
	CarRepo carRepo;

	@Autowired
	TradeDealRepo tradeDealRepo;

	@Autowired
	CarsService carsService;

	@Test
	void addPerson_newValidPerson_Success() {
		assertEquals(personDto, carsService.addPerson(personDto));
		CarOwner carOwner = carOwnerRepo.findById(personDto.id()).orElse(null);
		assertEquals(personDto, carOwner.build());
	}

	@Test
	void addPerson_samePerson_IllegalPersonsState() {
		assertThrowsExactly(IllegalPersonsStateException.class, () -> carsService.addPerson(personDto1));
	}

	@Test
	void addCar_newValidCar_Success() {
		assertEquals(car4, carsService.addCar(car4));
		CarDto carNoModel = new CarDto("11111111111", MODEL2, 2018, "green", 100000, CarState.OLD);
		assertThrowsExactly(IllegalCarsStateException.class, () -> carsService.addCar(car1));
		assertThrowsExactly(ModelNotFoundException.class, () -> carsService.addCar(carNoModel));
	}

	@Test
	void addCar_sameCar_IllegalCarsState() {
		assertEquals(car4, carsService.addCar(car4));
		assertThrowsExactly(IllegalCarsStateException.class, () -> carsService.addCar(car1));
	}

	@Test
	void testAddModel() {
		ModelDto modelDtoNew = new ModelDto(MODEL4, 2024, "Company1", 100, 2000);
		assertEquals(modelDtoNew, carsService.addModel(modelDtoNew));
		assertThrowsExactly(IllegalModelsStateException.class, () -> carsService.addModel(modelDtoNew));
	}

	@Test
	void testUpdatePerson() {
		PersonDto personUpdated = new PersonDto(PERSON_ID_1, NAME1, BIRTH_DATE_1, NEW_EMAIL);
		assertEquals(personUpdated, carsService.updatePerson(personUpdated));
		assertEquals(NEW_EMAIL, carOwnerRepo.findById(PERSON_ID_1).get().getEmail());
		assertThrowsExactly(PersonNotFoundException.class, () -> carsService.updatePerson(personDto));
	}

	@Test
	void testDeletePerson() {
		assertEquals(personDto1, carsService.deletePerson(PERSON_ID_1));
		assertThrowsExactly(PersonNotFoundException.class, () -> carsService.deletePerson(PERSON_ID_1));
	}

	@Test
	void testDeleteCar() {
		assertEquals(car1, carsService.deleteCar(CAR_NUMBER_1));
		assertThrowsExactly(CarNotFoundException.class, () -> carsService.deleteCar(CAR_NUMBER_1));
	}

	@Test
	void purchase_NewCarOwner_WithOldOwner() {
		int countDeals = (int) tradeDealRepo.count();
		TradeDealDto tradeDealDto = new TradeDealDto(CAR_NUMBER_1, PERSON_ID_2, DATE_TRADE_DEAL_1);
		assertEquals(tradeDealDto, carsService.purchase(tradeDealDto));
		assertEquals(PERSON_ID_2, carRepo.findById(CAR_NUMBER_1).get().getCarOwner().getId());
		TradeDeal tradeDeal = tradeDealRepo.findAll().get(countDeals);
		assertEquals(CAR_NUMBER_1, tradeDeal.getCar().getNumber());
		assertEquals(PERSON_ID_2, tradeDeal.getCarOwner().getId());
		assertEquals(DATE_TRADE_DEAL_1, tradeDeal.getDate().toString());
	}

	@Test
	void purchase_NewCarOwner_WithoutOldOwner() {
		int countDeals = (int) tradeDealRepo.count();
		carsService.addCar(car4);
		TradeDealDto tradeDealDto = new TradeDealDto(CAR_NUMBER_4, PERSON_ID_2, DATE_TRADE_DEAL_1);
		assertEquals(tradeDealDto, carsService.purchase(tradeDealDto));
		assertEquals(PERSON_ID_2, carRepo.findById(CAR_NUMBER_4).get().getCarOwner().getId());
		TradeDeal tradeDeal = tradeDealRepo.findAll().get(countDeals);
		assertEquals(CAR_NUMBER_4, tradeDeal.getCar().getNumber());
		assertEquals(PERSON_ID_2, tradeDeal.getCarOwner().getId());
		assertEquals(DATE_TRADE_DEAL_1, tradeDeal.getDate().toString());
	}

	@Test
	void purchase_NotFound() {
		TradeDealDto tradeDealCarNotFound = new TradeDealDto(CAR_NUMBER_4, PERSON_ID_1, DATE_TRADE_DEAL_1);
		TradeDealDto tradeDealOwnerNotFound = new TradeDealDto(CAR_NUMBER_1, PERSON_ID_NOT_EXISTS, DATE_TRADE_DEAL_1);
		assertThrowsExactly(PersonNotFoundException.class, () -> carsService.purchase(tradeDealOwnerNotFound));
		assertThrowsExactly(CarNotFoundException.class, () -> carsService.purchase(tradeDealCarNotFound));
	}

	@Test
	void purchase_NoNewCarOwner() {
		int countDeals = (int) tradeDealRepo.count();
		TradeDealDto tradeDealDto = new TradeDealDto(CAR_NUMBER_1, null, DATE_TRADE_DEAL_1);
		assertEquals(tradeDealDto, carsService.purchase(tradeDealDto));
		assertNull(carRepo.findById(CAR_NUMBER_1).get().getCarOwner());
		TradeDeal tradeDeal = tradeDealRepo.findAll().get(countDeals);
		assertEquals(CAR_NUMBER_1, tradeDeal.getCar().getNumber());
		assertNull(tradeDeal.getCarOwner());
		assertEquals(DATE_TRADE_DEAL_1, tradeDeal.getDate().toString());
	}

	@Test
	void purchase_SameOwner() {
		TradeDealDto tradeDeal = new TradeDealDto(CAR_NUMBER_1, PERSON_ID_1, DATE_TRADE_DEAL_1);
		assertThrowsExactly(TradeDealIllegalStateException.class, () -> carsService.purchase(tradeDeal));
		carsService.addCar(car4);
		TradeDealDto tradeDealNoOwners = new TradeDealDto(CAR_NUMBER_4, null, DATE_TRADE_DEAL_1);
		assertThrowsExactly(TradeDealIllegalStateException.class, () -> carsService.purchase(tradeDealNoOwners));
	}

	@Test
	@Disabled
	void testGetOwnerCars() {
		Object actual[] = carsService.getOwnerCars(PERSON_ID_1).toArray();
		Object expected[] = { car1 };
		assertArrayEquals(expected, actual);

		Object expected1[] = { car1, car2 };
		carsService.purchase(new TradeDealDto(CAR_NUMBER_2, PERSON_ID_1, null));
		Object actual1[] = carsService.getOwnerCars(PERSON_ID_1).toArray();
		assertArrayEquals(expected1, actual1);
		assertThrowsExactly(NotFoundException.class, () -> carsService.getOwnerCars(PERSON_ID_NOT_EXISTS));
	}

	@Test
	@Disabled
	void testGetCarOwner() {
		assertEquals(personDto1, carsService.getCarOwner(CAR_NUMBER_1));
		assertThrowsExactly(NotFoundException.class, () -> carsService.getCarOwner(CAR_NUMBER_1));
	}

	@Test
	@Disabled
	void getMostPopularModels_preset_success() {
		assertEquals(List.of(MODEL1), carsService.mostPopularModels());
	}

	@Test
	@Disabled
	void getMostPopularModels_manyModels_success() {
		carsService.addCar(new CarDto("123", MODEL2, 2000, null, null, null));
		carsService.addCar(new CarDto("124", MODEL2, 2000, null, null, null));
		carsService.addCar(new CarDto("125", MODEL3, 2000, null, null, null));

		carsService.purchase(new TradeDealDto("123", PERSON_ID_1, null));
		carsService.purchase(new TradeDealDto("124", PERSON_ID_1, null));
		carsService.purchase(new TradeDealDto("125", PERSON_ID_1, null));

		List<String> mostPopularModels = carsService.mostPopularModels();
		String[] actual = mostPopularModels.toArray(String[]::new);
		Arrays.sort(actual);
		String[] expected = { MODEL1, MODEL2 };
		assertArrayEquals(expected, actual);
	}

	@Test
	@Disabled
	void getMostPopularModels_emptyMap_NotFound() {

	}

}