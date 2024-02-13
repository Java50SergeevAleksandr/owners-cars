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
			select color from cars where model_name=:model
			group by color order by count(*) desc, color asc limit 1
			""", nativeQuery = true)
	String findOneMostPopularColorModel(String model);

	@Query(value = """
			select min(engine_power) as power, min(engine_capacity) as capacity
			from (select * from car_owners where birth_date between :birthDateFrom and :birthDateTo) owner_age
			join cars on id = owner_id
			join models on cars.model_name = models.model_name
			""", nativeQuery = true)
	EnginePowerCapacity findMinPowerCapacityOwnerBirthDates(LocalDate birthDateFrom, LocalDate birthDateTo);
}