package telran.cars;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.jdbc.Sql;
import telran.cars.repo.*;
import telran.cars.dto.*;
import telran.cars.exceptions.*;

import telran.cars.service.CarsService;
import telran.cars.service.model.*;

record ModelNameAmountTest(String name, long amount) implements Comparable<ModelNameAmountTest> {

	@Override
	public int compareTo(ModelNameAmountTest o) {
		int res = Long.compare(o.amount, amount);
		if (res == 0) {
			res = name.compareTo(o.name);
		}
		return res;
	}
}

@SpringBootTest
@Sql(scripts = { "classpath:test_data.sql" })
class CarsServiceTest {
	private static final String SERVICE_TEST = "Service Test: ";

	private static final String MODEL1 = "model1";
	private static final String MODEL2 = "model2";
	private static final String MODEL3 = "model3";
	private static final String MODEL4 = "model4";

	private static final String CAR_NUMBER_1 = "111-11-111";
	private static final String CAR_NUMBER_2 = "222-11-111";
	private static final String CAR_NUMBER_3 = "333-11-111";
	private static final String CAR_NUMBER_4 = "444-44-444";
	private static final String CAR_NUMBER_5 = "555-55-555";

	private static final String NAME1 = "name1";
	private static final String NAME2 = "name2";
	private static final String NAME3 = "name3";
	private static final String NAME4 = "name4";
	private static final String NAME5 = "name5";

	private static final String BIRTH_DATE_1 = "2000-10-10";
	private static final String BIRTH_DATE_2 = "2000-10-10";
	private static final String BIRTH_DATE_3 = "1970-01-01";
	private static final String BIRTH_DATE_4 = "1975-10-10";
	private static final String BIRTH_DATE_5 = "2004-10-10";

	private static final Long PERSON_ID_1 = 123l;
	private static final Long PERSON_ID_2 = 124l;
	private static final Long PERSON_ID_3 = 125l;
	private static final Long PERSON_ID_4 = 126l;
	private static final Long PERSON_ID_5 = 127l;
	private static final Long PERSON_ID_NOT_EXISTS = 1111111111L;

	private static final String NEW_EMAIL = "name1@tel-ran.co.il";
	private static final String EMAIL1 = "name1@gmail.com";
	private static final String EMAIL2 = "name2@gmail.com";
	private static final String EMAIL3 = "name3@gmail.com";
	private static final String EMAIL4 = "name4@gmail.com";
	private static final String EMAIL5 = "name5@gmail.com";

	private static final String DATE_TRADE_DEAL_1 = "2023-03-10";
	private static final String DATE_TRADE_DEAL_2 = "2023-03-20";
	private static final String DATE_TRADE_DEAL_3 = "2023-04-0";
	private static final String DATE_TRADE_DEAL_4 = "2023-11-20";
	private static final String DATE_TRADE_DEAL_5 = "2023-11-20";

	CarDto car1 = new CarDto(CAR_NUMBER_1, MODEL1, 2020, "red", 1000, CarState.GOOD);
	CarDto car2 = new CarDto(CAR_NUMBER_2, MODEL1, 2020, "silver", 10000, CarState.OLD);
	CarDto car3 = new CarDto(CAR_NUMBER_3, MODEL4, 2023, "white", 0, CarState.NEW);
	CarDto car4 = new CarDto(CAR_NUMBER_4, MODEL4, 2023, "black", 0, CarState.NEW);
	CarDto car5 = new CarDto(CAR_NUMBER_5, MODEL3, 2021, "silver", 5000, CarState.MIDDLE);

	PersonDto personDto = new PersonDto(PERSON_ID_NOT_EXISTS, NAME1, BIRTH_DATE_1, EMAIL1);
	PersonDto personDto1 = new PersonDto(PERSON_ID_1, NAME1, BIRTH_DATE_1, EMAIL1);
	PersonDto personDto2 = new PersonDto(PERSON_ID_2, NAME2, BIRTH_DATE_2, EMAIL2);
	PersonDto personDto3 = new PersonDto(PERSON_ID_3, NAME3, BIRTH_DATE_3, EMAIL3);
	PersonDto personDto4 = new PersonDto(PERSON_ID_4, NAME4, BIRTH_DATE_4, EMAIL4);
	PersonDto personDto5 = new PersonDto(PERSON_ID_5, NAME5, BIRTH_DATE_5, EMAIL5);

	TradeDealDto tradeDealDto4 = new TradeDealDto(CAR_NUMBER_4, PERSON_ID_4, DATE_TRADE_DEAL_4);
	TradeDealDto tradeDealDto5 = new TradeDealDto(CAR_NUMBER_5, PERSON_ID_5, DATE_TRADE_DEAL_5);
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
	/**
	 * test of the method getOwnerCars the method has been written at CW #64
	 */
	void testGetOwnerCars() {
		CarDto[] exp = { car1 };
		List<CarDto> res = carsService.getOwnerCars(PERSON_ID_1);
		Object[] arrCars = res.toArray();
		assertArrayEquals(exp, arrCars);

		CarDto[] exp1 = { car1, car2 };
		TradeDealDto tradeDeal = new TradeDealDto(CAR_NUMBER_2, PERSON_ID_1, DATE_TRADE_DEAL_1);
		carsService.purchase(tradeDeal);
		res = carsService.getOwnerCars(PERSON_ID_1);
		arrCars = res.toArray();
		assertArrayEquals(exp1, arrCars);

		carsService.deleteCar(CAR_NUMBER_1);
		carsService.deleteCar(CAR_NUMBER_2);
		res = carsService.getOwnerCars(PERSON_ID_1);
		assertTrue(res.isEmpty());
	}

	@Test
	/**
	 * test of the method getCarOwner the method has been written at CW #64
	 */
	void testGetCarOwner() {
		carsService.addCar(car4);
		assertNull(carsService.getCarOwner(CAR_NUMBER_4));
		assertEquals(personDto1, carsService.getCarOwner(CAR_NUMBER_1));
		assertThrowsExactly(CarNotFoundException.class, () -> carsService.getCarOwner(CAR_NUMBER_5));
	}

	@Test
	/**
	 * test of the method mostSoldModelNames the method has been written at CW #64
	 */
	@DisplayName(SERVICE_TEST + TestNames.MODEL_NAMES_MOST_SOLD)
	void testMostSoldModelNames() {
		setUpAddInfo();
		List<String> modelNamesActual = carsService.mostSoldModelNames();
		String[] modelNamesExpected = { "model1", "model4" };
		assertArrayEquals(modelNamesExpected, modelNamesActual.toArray(String[]::new));

	}

	private void setUpAddInfo() {
		carsService.addCar(car4);
		carsService.addCar(car5);
		carsService.addPerson(personDto4);
		carsService.addPerson(personDto5);
		carsService.purchase(tradeDealDto4);
		carsService.purchase(tradeDealDto5);
	}

	@Test
	/**
	 * test of the method mostPopularModelNames the method has been written at CW
	 * #64
	 */
	@DisplayName(SERVICE_TEST + TestNames.MODEL_NAMES_MOST_POPULAR)
	void testMostPopularModelNames() {
		setUpAddInfo();	
		List<ModelNameAmount> list = carsService.mostPopularModelNames(2);
		ModelNameAmountTest[] expected = { new ModelNameAmountTest(MODEL1, 2), new ModelNameAmountTest(MODEL4, 2) };
		modelNameAmountsTest(list, expected);

	}

	private void modelNameAmountsTest(List<ModelNameAmount> list, ModelNameAmountTest[] expected) {
		ModelNameAmountTest[] actual = list.stream().map(ma -> new ModelNameAmountTest(ma.getName(), ma.getAmount()))
				.sorted().toArray(ModelNameAmountTest[]::new);

		assertArrayEquals(expected, actual);
	}

	@Test
	@DisplayName(SERVICE_TEST + TestNames.TRADE_DEALS_COUNT_MONTH_MODEL)
	void testCountTradeDealAtMonthModel() {
		setUpAddInfo();
		assertEquals(2, carsService.countTradeDealAtMonthModel(MODEL1, 3, 2023));
	}

	@Test
	@DisplayName(SERVICE_TEST + TestNames.MODEL_NAMES_MOST_POPULAR_OWNER_AGES)
	void testMostPopularModelNameByOwnerAges() {
		setUpAddInfo();
		List<ModelNameAmount> list = carsService.mostPopularModelNameByOwnerAges(2, 30, 60);
		ModelNameAmountTest[] expected = { new ModelNameAmountTest(MODEL4, 2), new ModelNameAmountTest(MODEL1, 1) };
		modelNameAmountsTest(list, expected);
	}

	@Test
	@DisplayName(SERVICE_TEST + TestNames.COLOR_MOST_POPULR_MODEL)
	void testOneMostPopularColorModel() {
		setUpAddInfo();
		assertEquals("red", carsService.oneMostPopularColorModel(MODEL1));
	}

	@Test
	@DisplayName(SERVICE_TEST + TestNames.ENGINE_POWER_CAPACITY_MIN_AGES)
	void testMinEnginePowerCapacityByOwnerAges() {
		setUpAddInfo();
		EnginePowerCapacity info = carsService.minEnginePowerCapacityByOwnerAges(30, 60);
		int minPowerActual = info.getPower();
		int minCapacityActual = info.getCapacity();
		int minPowerExpected = 84;
		int minCapacityExpected = 1300;
		assertEquals(minPowerExpected, minPowerActual);
		assertEquals(minCapacityExpected, minCapacityActual);
	}

}