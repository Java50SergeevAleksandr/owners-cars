package telran.cars.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.cars.dto.ModelNameAmount;
import telran.cars.service.model.*;

public interface ModelRepo extends JpaRepository<Model, ModelYear> {
	@Query("""
			select car.model.modelYear.name as name from TradeDeal group by car.model.modelYear.name having count(*) =
			(select max(count) from 
			(select count(*) as count from TradeDeal 
			group by car.model.modelYear.name))
			""")
	List<String> findMostSoldModelNames();

	@Query("""
			select model.modelYear.name as name, count(*) as amount
			from Cars group by model.modelYear.name order by count(*) desc limit :nModels
			""")
	List<ModelNameAmount> findMostPopularModelNames(int nModels);

	@Query("""
			select model.modelYear.name as name, count(*) as amount
			from Car where carOwner.birthDate between :birthDateFrom and :birthDateTo
			group by model.modelYear.name order by count(*) desc limit :nModels
			""")
	List<ModelNameAmount> findPopularModelNameOwnerAges(int nModels, LocalDate birthDateFrom, LocalDate birthDateTo);
}