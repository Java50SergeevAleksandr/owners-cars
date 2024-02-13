package telran.cars.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.cars.dto.EnginePowerCapacity;
import telran.cars.service.model.*;

public interface CarRepo extends JpaRepository<Car, String> {
	List<Car> findByCarOwnerId(long id);

	@Query(value = """
			select car.color from Car car where model.modelYear.name = :model
			group by color order by count(*) desc, car.color asc limit 1
			""", nativeQuery = false)
	String findOneMostPopularColorModel(String model);

	@Query(value = """
			select min(car.model.enginePower) as power, min(car.model.engineCapacity) as capacity
			from Car car where carOwner.birthDate between :birthDateFrom and :birthDateTo
			""", nativeQuery = true)
	EnginePowerCapacity findMinPowerCapacityOwnerBirthDates(LocalDate birthDateFrom, LocalDate birthDateTo);
}