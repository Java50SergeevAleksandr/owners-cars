package telran.cars.service.model;

import java.time.LocalDate;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "trade_deals")
@Getter
public class TradeDeal {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;

	@ManyToOne
	@JoinColumn(name = "car_number", nullable = false)
	@Setter
	@OnDelete(action = OnDeleteAction.CASCADE)
	Car car;

	@ManyToOne
	@JoinColumn(name = "owner_id")
	@Setter
	@OnDelete(action = OnDeleteAction.SET_NULL)
	CarOwner carOwner;

	@Temporal(TemporalType.DATE)
	@Setter
	LocalDate date;
}