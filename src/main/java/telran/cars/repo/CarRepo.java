package telran.cars.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.cars.service.model.*;

public interface CarRepo extends JpaRepository<Car, String> {
	Car findByCarOwnerId(long id);
}