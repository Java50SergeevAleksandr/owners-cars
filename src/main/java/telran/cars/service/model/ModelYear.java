package telran.cars.service.model;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.*;

@Embeddable
@EqualsAndHashCode
@AllArgsConstructor
@Getter
public class ModelYear implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name = "model_name")
	String name;
	
	@Column(name = "model_year")
	int year;

}
