package telran.cars.service.model;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "trade_deals")
public class TradeDeal {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;

	@ManyToOne
	@JoinColumn(name = "car_number", nullable = false)
	Car car;

	@ManyToOne
	@JoinColumn(name = "owner_id")
	CarOwner carOwner;

	@Temporal(TemporalType.DATE)
	LocalDate date;
}