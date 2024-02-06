package telran.cars.service.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import telran.cars.dto.PersonDto;
import jakarta.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "car_owners")
public class CarOwner {
	@Id
	Long id;
	String name;

	@Column(nullable = false, name = "birth_date")
	@Temporal(TemporalType.DATE)
	LocalDate birthDate;

	String email;

	public CarOwner(PersonDto personDto) {
		id = personDto.id();
		name = personDto.name();
		birthDate = LocalDate.parse(personDto.birthDate());
		email = personDto.email();
	}

	public PersonDto build() {
		return new PersonDto(id, name, birthDate.toString(), email);
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
