package telran.cars.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.cars.dto.ModelNameAmount;
import telran.cars.service.model.*;

public interface ModelRepo extends JpaRepository<Model, ModelYear> {
	@Query(value = "select model_name from cars join trade_deals td"
			+ " on cars.car_number=td.car_number group by model_name " + " having count(*) = (select max(count) from "
			+ "(select count(*) as count from cars join trade_deals "
			+ " on cars.car_number = trade_deals.car_number)) ", nativeQuery = true) // just SQL query
	List<String> findMostSoldModelNames();

	@Query(value = "select c.model_name as name, count(*) as amount "   // as name, as amount important to match with ModelNameAmount fields namings
			+ "from cars c group by c.model_name order by count(*) desc limit :nModels", nativeQuery = true)
	List<ModelNameAmount> findMostPopularModelNames(int nModels);
}