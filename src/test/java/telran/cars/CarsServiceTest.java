package telran.cars;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import telran.cars.dto.*;
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
	CarsService carsService;

	@BeforeEach
	void setUp() {
		carsService.clearAll();
		carsService.addCar(car1);
		carsService.addCar(car2);
		carsService.addPerson(personDto1);
		carsService.addPerson(personDto2);
		carsService.purchase(new TradeDealDto(CAR_NUMBER_1, PERSON_ID_1));
		carsService.purchase(new TradeDealDto(CAR_NUMBER_2, PERSON_ID_2));

	}	

	@Test
	void testAddPerson() {
		assertEquals(personDto, carsService.addPerson(personDto));
		assertThrowsExactly(IllegalStateException.class, () -> carsService.addPerson(personDto1));
	}

	@Test
	void testAddCar() {
		assertEquals(car, carsService.addCar(car));
		assertThrowsExactly(IllegalStateException.class, () -> carsService.addCar(car1));
	}

	@Test
	void testUpdatePerson() {
		assertEquals(personDto1, carsService.updatePerson(personDto1Updated));
		assertThrowsExactly(IllegalStateException.class, () -> carsService.updatePerson(personDto));
	}

	@Test
	void testDeletePerson() {
		assertEquals(personDto1, carsService.deletePerson(PERSON_ID_1));
		assertThrowsExactly(IllegalStateException.class, () -> carsService.deletePerson(PERSON_ID_1));
	}

	@Test
	void testDeleteCar() {
		assertEquals(car1, carsService.deleteCar(CAR_NUMBER_1));
		assertThrowsExactly(IllegalStateException.class, () -> carsService.deleteCar(CAR_NUMBER_1));
	}

	@Test
	void testPurchase() {
		TradeDealDto td = new TradeDealDto(CAR_NUMBER, PERSON_ID_1);
		TradeDealDto td1 = new TradeDealDto(CAR_NUMBER_1, PERSON_ID_NOT_EXISTS);
		TradeDealDto td2 = new TradeDealDto(CAR_NUMBER_1, null);
		assertThrowsExactly(IllegalStateException.class, () -> carsService.purchase(td));
		//assertThrowsExactly(IllegalStateException.class, () -> carsService.purchase(td1));
		assertEquals(td1, carsService.purchase(td1));
		assertEquals(td2, carsService.purchase(td2));

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

	}

	@Test
	void testGetCarOwner() {
		assertEquals(personDto1, carsService.getCarOwner(CAR_NUMBER_1));
		assertThrowsExactly(IllegalStateException.class, () -> carsService.getCarOwner(CAR_NUMBER));
	}

}