package telran.cars.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.cars.service.model.*;

public interface TradeDealRepo extends JpaRepository<TradeDeal, Long> {
	List<TradeDeal> findByCarNumber(String carNumber);

	List<TradeDeal> findByCarOwnerId(long id);

	long countByCarModelModelYearNameAndDateBetween(String modelName, LocalDate date1, LocalDate date2);
}