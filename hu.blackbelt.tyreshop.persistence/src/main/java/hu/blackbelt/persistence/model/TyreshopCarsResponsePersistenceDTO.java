package hu.blackbelt.persistence.model;

import hu.blackbelt.core.persistence.entity.Car;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class TyreshopCarsResponsePersistenceDTO {
    private List<Car> cars;
}
