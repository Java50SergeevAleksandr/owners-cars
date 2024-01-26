package telran.cars;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import telran.cars.dto.CarDto;
import telran.cars.dto.PersonDto;
import telran.cars.dto.TradeDealDto;
import telran.cars.exceptions.NotFoundException;
import telran.cars.service.CarsService;

@WebMvcTest // inserting into Application Context Mock WEB server instead of real WebServer
class CarsControllerTest {
	private static final long PERSON_ID = 123l;
	private static final String CAR_NUMBER = "123-01-002";
	private static final String PERSON_NOT_FOUND_MESSAGE = "person not found message";
	private static final String CAR_NOT_FOUND_MESSAGE = "car not found message";
	private static final String CAR_ALREADY_EXISTS_MESSAGE = "car already exists message";
	private static final String PERSON_ALREADY_EXISTS_MESSAGE = "person already exists message";
	@MockBean // inserting into Application Context Mock instead of real Service
				// implementation
	CarsService carsService;
	@Autowired // for injection of MockMvc from Application Context
	MockMvc mockMvc;
	CarDto carDto = new CarDto(CAR_NUMBER, "model");
	CarDto carDto1 = new CarDto("car123", "mode123");

	@Autowired // for injection of ObjectMapper from Application context
	ObjectMapper mapper; // object for getting JSON from object and object from JSON
	private PersonDto personDto = new PersonDto(PERSON_ID, "Vasya", "2000-10-10", "vasya@gmail.com");
	PersonDto personDtoUpdated = new PersonDto(PERSON_ID, "Vasya", "2000-10-10", "vasya@tel-ran.com");
	PersonDto personWrongEmail = new PersonDto(PERSON_ID, "Vasya", "2000-10-10", "kuku");

	TradeDealDto tradeDeal = new TradeDealDto(CAR_NUMBER, PERSON_ID);

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
	void testDeletePersonNotFound() throws Exception {
		when(carsService.deletePerson(PERSON_ID)).thenThrow(new NotFoundException(PERSON_NOT_FOUND_MESSAGE));

		String rsponse = mockMvc.perform(delete("http://localhost:8080/cars/person/" + PERSON_ID))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
		assertEquals(PERSON_NOT_FOUND_MESSAGE, rsponse);

	}

	@Test
	void testGetCarOwnerNotFound() throws Exception {
		when(carsService.getCarOwner(CAR_NUMBER)).thenThrow(new NotFoundException(PERSON_NOT_FOUND_MESSAGE));
		String response = mockMvc.perform(get("http://localhost:8080/cars/" + CAR_NUMBER))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
		assertEquals(PERSON_NOT_FOUND_MESSAGE, response);
	}

	@Test
	void testGetOwnerCarsPersonNotFound() throws Exception {

		when(carsService.getOwnerCars(PERSON_ID)).thenThrow(new NotFoundException(PERSON_NOT_FOUND_MESSAGE));
		String response = mockMvc.perform(get("http://localhost:8080/cars/person/" + PERSON_ID))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
		assertEquals(PERSON_NOT_FOUND_MESSAGE, response);
	}

	@Test
	void testDeleteCarNotFound() throws Exception {
		when(carsService.deleteCar(CAR_NUMBER)).thenThrow(new NotFoundException(CAR_NOT_FOUND_MESSAGE));
		String response = mockMvc.perform(delete("http://localhost:8080/cars/" + CAR_NUMBER))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
		assertEquals(CAR_NOT_FOUND_MESSAGE, response);
	}

	@Test
	void testAddCarAlreadyExists() throws Exception {
		String jsonExpected = mapper.writeValueAsString(carDto);
		when(carsService.addCar(carDto)).thenThrow(new IllegalStateException(CAR_ALREADY_EXISTS_MESSAGE));
		String actualJSON = mockMvc
				.perform(post("http://localhost:8080/cars").contentType(MediaType.APPLICATION_JSON)
						.content(jsonExpected))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertEquals(CAR_ALREADY_EXISTS_MESSAGE, actualJSON);
	}

	@Test
	void testAddPersonAlreadyExists() throws Exception {
		String jsonExpected = mapper.writeValueAsString(personDto);
		when(carsService.addPerson(personDto)).thenThrow(new IllegalStateException(PERSON_ALREADY_EXISTS_MESSAGE));
		String actualJSON = mockMvc
				.perform(post("http://localhost:8080/cars/person").contentType(MediaType.APPLICATION_JSON)
						.content(jsonExpected))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertEquals(PERSON_ALREADY_EXISTS_MESSAGE, actualJSON);
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

	private void testPurchaseNotFound(String message) throws Exception {
		when(carsService.purchase(tradeDeal)).thenThrow(new NotFoundException(message));
		String jsonTradeDeal = mapper.writeValueAsString(tradeDeal);
		String response = mockMvc
				.perform(put("http://localhost:8080/cars/trade").contentType(MediaType.APPLICATION_JSON)
						.content(jsonTradeDeal))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();
		assertEquals(message, response);
	}
}