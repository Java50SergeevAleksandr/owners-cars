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
	String name;
	int year;

}
