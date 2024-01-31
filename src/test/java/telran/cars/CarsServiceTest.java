package telran.cars;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import telran.cars.dto.*;
import telran.cars.exceptions.NotFoundException;
import telran.cars.service.CarsService;

@SpringBootTest
class CarsServiceTest {
	private static final String MODEL = "model";
	private static final String CAR_NUMBER = "101-10-101";
	private static final String CAR_NUMBER_1 = "111-11-111";
	private static final String CAR_NUMBER_2 = "222-22-222";
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
	CarDto car = new CarDto(CAR_NUMBER, MODEL);
	CarDto car1 = new CarDto(CAR_NUMBER_1, MODEL);
	CarDto car2 = new CarDto(CAR_NUMBER_2, MODEL);
	PersonDto personDto = new PersonDto(PERSON_ID_NOT_EXISTS, NAME1, BIRTH_DATE_1, EMAIL1);
	PersonDto personDto1 = new PersonDto(PERSON_ID_1, NAME1, BIRTH_DATE_1, EMAIL1);
	PersonDto personDto1Updated = new PersonDto(PERSON_ID_1, NAME2, BIRTH_DATE_1, EMAIL1);
	PersonDto personDto2 = new PersonDto(PERSON_ID_2, NAME2, BIRTH_DATE_2, EMAIL2);
	
	@Autowired
	ApplicationContext ctx;
	
	CarsService carsService;

	@BeforeEach
	void setUp() {
		carsService = ctx.getBean("carsService", CarsService.class);
		carsService.addCar(car1);
		carsService.addCar(car2);
		carsService.addPerson(personDto1);
		carsService.addPerson(personDto2);
		carsService.purchase(new TradeDealDto(CAR_NUMBER_1, PERSON_ID_1));
		carsService.purchase(new TradeDealDto(CAR_NUMBER_2, PERSON_ID_2));

	}

	@Test
	void AddPerson_newValidPerson_Success() {
		assertEquals(personDto, carsService.addPerson(personDto));
		List<CarDto> cars = carsService.getOwnerCars(personDto.id());
		assertTrue(cars.isEmpty());
	}

	@Test
	void AddPerson_samePerson_IllegalState() {
		assertThrowsExactly(IllegalStateException.class, () -> carsService.addPerson(personDto1));
	}

	@Test
	void AddCar_newValidCar_Success() {
		assertEquals(car, carsService.addCar(car));
		PersonDto person = carsService.getCarOwner(CAR_NUMBER);
		assertNull(person);
	}

	@Test
	void AddCar_sameCar_IllegalState() {
		assertThrowsExactly(IllegalStateException.class, () -> carsService.addCar(car1));
	}

	@Test
	void testUpdatePerson() {
		PersonDto personUpdated = new PersonDto(PERSON_ID_1, NAME1, BIRTH_DATE_1, NEW_EMAIL);
		assertEquals(personUpdated, carsService.updatePerson(personUpdated));
		assertEquals(personUpdated, carsService.getCarOwner(CAR_NUMBER_1));
		assertThrowsExactly(NotFoundException.class, () -> carsService.updatePerson(personDto));
	}

	@Test
	void testDeletePerson() {
		List<CarDto> cars = carsService.getOwnerCars(PERSON_ID_1);
		assertEquals(personDto1, carsService.deletePerson(PERSON_ID_1));
		assertThrowsExactly(NotFoundException.class, () -> carsService.deletePerson(PERSON_ID_1));
		cars.forEach(c -> assertNull(carsService.getCarOwner(c.number())));
	}

	@Test
	void testDeleteCar() {
		Long id = carsService.getCarOwner(CAR_NUMBER_1).id();
		assertEquals(car1, carsService.deleteCar(CAR_NUMBER_1));
		assertThrowsExactly(NotFoundException.class, () -> carsService.deleteCar(CAR_NUMBER_1));
		assertFalse(carsService.getOwnerCars(id).contains(car1));
	}

	@Test
	void DeleteCar_withoutOwner_succes() {
		carsService.addCar(car);
		assertEquals(car, carsService.deleteCar(CAR_NUMBER));
	}

	@Test
	void Purchase_NewCarOnwer() {
		TradeDealDto tradeDeal = new TradeDealDto(CAR_NUMBER_1, PERSON_ID_2);
		assertEquals(tradeDeal, carsService.purchase(tradeDeal));
		assertEquals(personDto2, carsService.getCarOwner(CAR_NUMBER_1));
		assertFalse(carsService.getOwnerCars(PERSON_ID_1).contains(car1));
		assertTrue(carsService.getOwnerCars(PERSON_ID_2).contains(car1));

	}

	@Test
	void Purchase_NotFound() {
		TradeDealDto tradeDealCarNotFound = new TradeDealDto(CAR_NUMBER, PERSON_ID_1);
		TradeDealDto tradeDealOwnerNotFound = new TradeDealDto(CAR_NUMBER_1, PERSON_ID_NOT_EXISTS);
		assertThrowsExactly(NotFoundException.class, () -> carsService.purchase(tradeDealOwnerNotFound));
		assertThrowsExactly(NotFoundException.class, () -> carsService.purchase(tradeDealCarNotFound));

	}

	@Test
	void Purchase_NoCarOwner() {
		TradeDealDto tradeDeal = new TradeDealDto(CAR_NUMBER_1, null);
		assertEquals(tradeDeal, carsService.purchase(tradeDeal));
		assertFalse(carsService.getOwnerCars(PERSON_ID_1).contains(car1));
		assertNull(carsService.getCarOwner(CAR_NUMBER_1));
	}

	@Test
	void Purchase_SameOwner() {
		TradeDealDto tradeDeal = new TradeDealDto(CAR_NUMBER_1, PERSON_ID_1);
		assertThrowsExactly(IllegalStateException.class, () -> carsService.purchase(tradeDeal));
	}

	@Test
	void testGetOwnerCars() {
		Object actual[] = carsService.getOwnerCars(PERSON_ID_1).toArray();
		Object expected[] = { car1 };
		assertArrayEquals(expected, actual);

		Object expected1[] = { car1, car2 };
		carsService.purchase(new TradeDealDto(CAR_NUMBER_2, PERSON_ID_1));
		Object actual1[] = carsService.getOwnerCars(PERSON_ID_1).toArray();
		assertArrayEquals(expected1, actual1);
		assertThrowsExactly(NotFoundException.class, () -> carsService.getOwnerCars(PERSON_ID_NOT_EXISTS));
	}

	@Test
	void testGetCarOwner() {
		assertEquals(personDto1, carsService.getCarOwner(CAR_NUMBER_1));
		assertThrowsExactly(NotFoundException.class, () -> carsService.getCarOwner(CAR_NUMBER));
	}

}