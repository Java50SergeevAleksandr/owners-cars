package telran.cars.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.cars.service.model.*;

public interface ModelRepo extends JpaRepository<Model, ModelYear> {

}