package telran.cars;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import telran.cars.dto.CarDto;
import telran.cars.service.CarsService;

@WebMvcTest // inserting into Application Context Mock WEB server instead of real WebServer
class CarsControllerTest {
	@MockBean // inserting into Application Context Mock instead of real Service
				// implementation
	CarsService carsService;
	@Autowired // for injection of MockMvc from Application Context
	MockMvc mockMvc;
	CarDto carDto = new CarDto("car", "model");
	CarDto carDto1 = new CarDto("car123", "mode123");
	@Autowired // for injection of ObjectMapper from Application context
	ObjectMapper mapper; // object for getting JSON from object and object from JSON

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
	void testAddPerson() {
		// TODO
	}

	@Test
	void testUpdatePerson() {
		// Not IMplemented
	}

	@Test
	void testPurchase() {
		// Not IMplemented
	}

	@Test
	void testDeletePerson() {
		// Not IMplemented
	}

	@Test
	void testDeleteCar() {
		// Not IMplemented
	}

	@Test
	void testGetOwnerCars() {
		// Not IMplemented
	}

	@Test
	void testGetCarOwner() {
		// Not IMplemented
	}

}
