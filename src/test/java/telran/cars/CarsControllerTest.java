package telran.cars;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import telran.cars.dto.*;
import static telran.cars.api.ValidationConstants.*;
import telran.cars.exceptions.NotFoundException;
import telran.cars.exceptions.controller.CarsExceptionsController;
import telran.cars.service.CarsService;

//record for mismatch type test
record PersonDtoIdString(String id, String name, String birthDate, String email) {

}

@WebMvcTest // inserting into Application Context Mock WEB server instead of real WebServer
class CarsControllerTest {
	private static final long PERSON_ID = 123000l;
	private static final String CAR_NUMBER = "123-01-002";
	private static final String PERSON_NOT_FOUND_MESSAGE = "person not found";
	private static final String PERSON_ALREADY_EXISTS_MESSAGE = "person already exists";
	private static final String CAR_ALREADY_EXISTS_MESSAGE = "car already exists";
	private static final String CAR_NOT_FOUND_MESSAGE = "car not found";
	static final String WRONG_EMAIL_ADDRESS = "kuku";
	static final String EMAIL_ADDRESS = "vasya@gmail.com";
	private static final long WRONG_PERSON_ID = 123L;
	static final String WRONG_PERSON_ID_TYPE = "abc";
	static final String WRONG_CAR_NUMBER = "kikuk";
	@MockBean // inserting into Application Context Mock instead of real Service
				// implementation
	CarsService carsService;
	@Autowired // for injection of MockMvc from Application Context
	MockMvc mockMvc;
	CarDto carDto = new CarDto(CAR_NUMBER, "model");
	CarDto carDto1 = new CarDto("car123", "mode123");
	CarDto carDtoMissingFields = new CarDto(null, null);

	@Autowired // for injection of ObjectMapper from Application context
	ObjectMapper mapper; // object for getting JSON from object and object from JSON
	private PersonDto personDto = new PersonDto(PERSON_ID, "Vasya", "2000-10-10", EMAIL_ADDRESS);
	PersonDto personDtoUpdated = new PersonDto(PERSON_ID, "Vasya", "2000-10-10", "vasya@tel-ran.com");
	PersonDto personWrongEmail = new PersonDto(PERSON_ID, "Vasya", "2000-10-10", WRONG_EMAIL_ADDRESS);
	PersonDto personNoId = new PersonDto(null, "Vasya", "2000-10-10", EMAIL_ADDRESS);
	PersonDto personWrongId = new PersonDto(100000000000l, "Vasya", "2000-10-10", EMAIL_ADDRESS);
	PersonDto personWrongBirthdate = new PersonDto(PERSON_ID, "Vasya", "2000-10", EMAIL_ADDRESS);
	TradeDealDto tradeDeal = new TradeDealDto(CAR_NUMBER, PERSON_ID);
	PersonDtoIdString personDtoWrongIdType = new PersonDtoIdString("abc", "Vasya", "2000-10-10", EMAIL_ADDRESS);
	PersonDto personAllFieldsMissing = new PersonDto(null, null, null, null);
	TradeDealDto tradeDealWrongCarNumber = new TradeDealDto(WRONG_CAR_NUMBER, PERSON_ID);
	TradeDealDto tradeDealWrongId = new TradeDealDto(CAR_NUMBER, -10l);
	TradeDealDto tradeDealAllFieldsMissing = new TradeDealDto(null, null);
	private String[] expectedCarMissingFieldsMessages = { MISSING_CAR_MODEL_MESSAGE, MISSING_CAR_NUMBER_MESSAGE };
	private String[] expectedPersonMissingFieldsMessages = { MISSING_BIRTH_DATE_MESSAGE, MISSING_PERSON_EMAIL,
			MISSING_PERSON_ID_MESSAGE, MISSING_PERSON_NAME_MESSAGE };

	@Test
	void testAddCar() throws Exception {
		when(carsService.addCar(carDto)).thenReturn(carDto);
		String jsonCarDto = mapper.writeValueAsString(carDto); // conversion from carDto object to string JSON
		String actualJSON = mockMvc
				.perform(post("http://localhost:8080/cars").contentType(MediaType.APPLICATION_JSON).content(jsonCarDto))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(jsonCarDto, actualJSON);

	}

	@Test
	void testAddPerson() throws Exception {
		when(carsService.addPerson(personDto)).thenReturn(personDto);
		String jsonPersonDto = mapper.writeValueAsString(personDto); // conversion from carDto object to string JSON
		String actualJSON = mockMvc
				.perform(post("http://localhost:8080/cars/person").contentType(MediaType.APPLICATION_JSON)
						.content(jsonPersonDto))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(jsonPersonDto, actualJSON);
	}

	@Test
	void testUpdatePerson() throws Exception {
		when(carsService.updatePerson(personDtoUpdated)).thenReturn(personDtoUpdated);
		String jsonPersonDtoUpdated = mapper.writeValueAsString(personDtoUpdated); // conversion from carDto object to
																					// string JSON
		String actualJSON = mockMvc
				.perform(put("http://localhost:8080/cars/person").contentType(MediaType.APPLICATION_JSON)
						.content(jsonPersonDtoUpdated))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(jsonPersonDtoUpdated, actualJSON);
	}

	@Test
	void testPurchase() throws Exception {
		when(carsService.purchase(tradeDeal)).thenReturn(tradeDeal);
		String jsonTradeDeal = mapper.writeValueAsString(tradeDeal);
		String actualJSON = mockMvc
				.perform(put("http://localhost:8080/cars/trade").contentType(MediaType.APPLICATION_JSON)
						.content(jsonTradeDeal))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(jsonTradeDeal, actualJSON);
	}

	@Test
	void testDeletePerson() throws Exception {
		when(carsService.deletePerson(PERSON_ID)).thenReturn(personDto);
		String jsonExpected = mapper.writeValueAsString(personDto);
		String actualJSON = mockMvc.perform(delete("http://localhost:8080/cars/person/" + PERSON_ID))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(jsonExpected, actualJSON);
	}

	@Test
	void testDeleteCar() throws Exception {
		when(carsService.deleteCar(CAR_NUMBER)).thenReturn(carDto);
		String jsonExpected = mapper.writeValueAsString(carDto);
		String actualJSON = mockMvc.perform(delete("http://localhost:8080/cars/" + CAR_NUMBER))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(jsonExpected, actualJSON);
	}

	@Test
	void testGetOwnerCars() throws Exception {
		CarDto[] expectedArray = { carDto, carDto1 };
		String jsonExpected = mapper.writeValueAsString(expectedArray);
		when(carsService.getOwnerCars(PERSON_ID)).thenReturn(Arrays.asList(expectedArray));
		String actualJSON = mockMvc.perform(get("http://localhost:8080/cars/person/" + PERSON_ID))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(jsonExpected, actualJSON);
	}

	@Test
	void testGetCarOwner() throws Exception {
		when(carsService.getCarOwner(CAR_NUMBER)).thenReturn(personDto);
		String jsonExpected = mapper.writeValueAsString(personDto);
		String actualJSON = mockMvc.perform(get("http://localhost:8080/cars/" + CAR_NUMBER)).andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		assertEquals(jsonExpected, actualJSON);
	}

	@Test
	void GetMostPopularModels_success() throws Exception {
		List<String> list = List.of("model1");
		String jsonExpected = mapper.writeValueAsString(list);
		when(carsService.mostPopularModels()).thenReturn(list);
		String responseJSON = mockMvc.perform(get("http://localhost:8080/cars/models")).andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		assertEquals(jsonExpected, responseJSON);

	}

	/******************************************************************/
	/*********** ALternative flows - Service Exceptions Handling *************/
	@Test
	void GetMostPopularModels_NotFound() throws Exception {
		when(carsService.mostPopularModels()).thenThrow(new NotFoundException(CAR_NOT_FOUND_MESSAGE));

		String response = mockMvc.perform(get("http://localhost:8080/cars/models"))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
		assertEquals(CAR_NOT_FOUND_MESSAGE, response);

	}

	@Test
	void testDeletePersonNotFound() throws Exception {
		when(carsService.deletePerson(PERSON_ID)).thenThrow(new NotFoundException(PERSON_NOT_FOUND_MESSAGE));

		String actualJSON = mockMvc.perform(delete("http://localhost:8080/cars/person/" + PERSON_ID))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
		assertEquals(PERSON_NOT_FOUND_MESSAGE, actualJSON);

	}

	@Test
	void testAddPersonAlreadyExists() throws Exception {
		when(carsService.addPerson(personDto)).thenThrow(new IllegalStateException(PERSON_ALREADY_EXISTS_MESSAGE));
		String jsonPersonDto = mapper.writeValueAsString(personDto); // conversion from carDto object to string JSON
		String response = mockMvc
				.perform(post("http://localhost:8080/cars/person").contentType(MediaType.APPLICATION_JSON)
						.content(jsonPersonDto))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertEquals(PERSON_ALREADY_EXISTS_MESSAGE, response);
	}

	@Test
	void testAddCarAlreadyExists() throws Exception {
		when(carsService.addCar(carDto)).thenThrow(new IllegalStateException(CAR_ALREADY_EXISTS_MESSAGE));
		String jsonCarDto = mapper.writeValueAsString(carDto); // conversion from carDto object to string JSON
		String response = mockMvc
				.perform(post("http://localhost:8080/cars").contentType(MediaType.APPLICATION_JSON).content(jsonCarDto))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertEquals(CAR_ALREADY_EXISTS_MESSAGE, response);

	}

	@Test
	void testUpdatePersonNotFound() throws Exception {
		when(carsService.updatePerson(personDtoUpdated)).thenThrow(new NotFoundException(PERSON_NOT_FOUND_MESSAGE));
		String jsonPersonDtoUpdated = mapper.writeValueAsString(personDtoUpdated); // conversion from carDto object to
																					// string JSON
		String response = mockMvc
				.perform(put("http://localhost:8080/cars/person").contentType(MediaType.APPLICATION_JSON)
						.content(jsonPersonDtoUpdated))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
		assertEquals(PERSON_NOT_FOUND_MESSAGE, response);
	}

	@Test
	void testPurchaseCarNotFound() throws Exception {
		testPurchaseNotFound(CAR_NOT_FOUND_MESSAGE);
	}

	@Test
	void testPurchasePersonNotFound() throws Exception {
		testPurchaseNotFound(PERSON_NOT_FOUND_MESSAGE);
	}

	private void testPurchaseNotFound(String message)
			throws JsonProcessingException, UnsupportedEncodingException, Exception {
		when(carsService.purchase(tradeDeal)).thenThrow(new NotFoundException(message));
		String jsonTradeDeal = mapper.writeValueAsString(tradeDeal);
		String response = mockMvc
				.perform(put("http://localhost:8080/cars/trade").contentType(MediaType.APPLICATION_JSON)
						.content(jsonTradeDeal))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
		assertEquals(message, response);
	}

	@Test
	void testDeleteCarNotFound() throws Exception {
		when(carsService.deleteCar(CAR_NUMBER)).thenThrow(new NotFoundException(CAR_NOT_FOUND_MESSAGE));
		String response = mockMvc.perform(delete("http://localhost:8080/cars/" + CAR_NUMBER))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
		assertEquals(CAR_NOT_FOUND_MESSAGE, response);
	}

	@Test
	void testGetOwnerCarsPersonNotFound() throws Exception {

		when(carsService.getOwnerCars(PERSON_ID)).thenThrow(new NotFoundException(PERSON_NOT_FOUND_MESSAGE));
		String response = mockMvc.perform(get("http://localhost:8080/cars/person/" + PERSON_ID))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
		assertEquals(PERSON_NOT_FOUND_MESSAGE, response);
	}

	@Test
	void testGetCarOwnerCarNotFound() throws Exception {
		when(carsService.getCarOwner(CAR_NUMBER)).thenThrow(new NotFoundException(CAR_NOT_FOUND_MESSAGE));
		String response = mockMvc.perform(get("http://localhost:8080/cars/" + CAR_NUMBER))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
		assertEquals(CAR_NOT_FOUND_MESSAGE, response);
	}

	/*****************************************************************************/
	/* Alternative flows - Validation exceptions handling ***********************/
	@Test
	void addPersonWrongEmailTest() throws Exception {
		wrongPersonDataRequest(personWrongEmail, WRONG_EMAIL_FORMAT);
	}

	@Test
	void addPersonWrongBirthDateTest() throws Exception {
		wrongPersonDataRequest(personWrongBirthdate, WRONG_DATE_FORMAT);
	}

	@Test
	void addPersonWrongIdTest() throws Exception {
		wrongPersonDataRequest(personWrongId, WRONG_MAX_PERSON_ID_VALUE);

	}

	@Test
	void addPersonWrongIdTypeTest() throws Exception {
		wrongPersonDataRequest(personDtoWrongIdType, CarsExceptionsController.JSON_TYPE_MISMATCH_MESSAGE);
	}

	@Test
	void purchaseWrongCarNumberTest() throws Exception {
		purchaseWrongData(tradeDealWrongCarNumber, WRONG_CAR_NUMBER_MESSAGE);
	}

	@Test
	void purchaseWrongPersonIdTest() throws Exception {
		purchaseWrongData(tradeDealWrongId, WRONG_MIN_PERSON_ID_VALUE);
	}

	@Test
	void deletePersonWrongIdTest() throws Exception {
		String response = mockMvc.perform(delete("http://localhost:8080/cars/person/" + WRONG_PERSON_ID))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertEquals(WRONG_MIN_PERSON_ID_VALUE, response);
	}

	@Test
	void deleteCarWrongCarNumber() throws Exception {
		String response = mockMvc.perform(delete("http://localhost:8080/cars/" + WRONG_CAR_NUMBER))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertEquals(WRONG_CAR_NUMBER_MESSAGE, response);
	}

	@Test
	void addCarMissingFields() throws Exception {
		String jsonCarDto = mapper.writeValueAsString(carDtoMissingFields); // conversion from carDto object to string
																			// JSON
		String response = mockMvc
				.perform(post("http://localhost:8080/cars").contentType(MediaType.APPLICATION_JSON).content(jsonCarDto))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		allFieldsMissingTest(expectedCarMissingFieldsMessages, response);
	}

	@Test
	void addPersonMissingFields() throws Exception {
		String jsonPersonDto = mapper.writeValueAsString(personAllFieldsMissing); // conversion from carDto object to
																					// string JSON
		String response = mockMvc
				.perform(post("http://localhost:8080/cars/person").contentType(MediaType.APPLICATION_JSON)
						.content(jsonPersonDto))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		allFieldsMissingTest(expectedPersonMissingFieldsMessages, response);
	}

	private void allFieldsMissingTest(String[] expectedMessages, String response) {
		Arrays.sort(expectedMessages);
		String[] actualMessages = response.split(";");
		Arrays.sort(actualMessages);
		assertArrayEquals(expectedMessages, actualMessages);
	}

	private void wrongPersonDataRequest(Object personDtoWrongData, String expectedMessage) throws Exception {
		String jsonPersonDto = mapper.writeValueAsString(personDtoWrongData); // conversion from carDto object to string
																				// JSON
		String response = mockMvc
				.perform(post("http://localhost:8080/cars/person").contentType(MediaType.APPLICATION_JSON)
						.content(jsonPersonDto))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertEquals(expectedMessage, response);
	}

	private void purchaseWrongData(TradeDealDto tradeDeal, String expectedMessage) throws Exception {
		String jsonTradeDeal = mapper.writeValueAsString(tradeDeal);
		String response = mockMvc
				.perform(put("http://localhost:8080/cars/trade").contentType(MediaType.APPLICATION_JSON)
						.content(jsonTradeDeal))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertEquals(expectedMessage, response);
	}

	@Test
	void testGetOwnerCarsMismatch() throws Exception {
		String response = mockMvc.perform(get("http://localhost:8080/cars/person/" + WRONG_PERSON_ID_TYPE))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertEquals(CarsExceptionsController.TYPE_MISMATCH_MESSAGE, response);
	}

}