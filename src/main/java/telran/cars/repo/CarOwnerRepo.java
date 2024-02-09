package telran.cars.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.cars.service.model.*;

public interface CarOwnerRepo extends JpaRepository<CarOwner, Long> {

}