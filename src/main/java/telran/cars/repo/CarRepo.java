package telran.cars.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.cars.service.model.*;

public interface CarRepo extends JpaRepository<Car, String> {
	List<Car> findByCarOwnerId(long id);	
}