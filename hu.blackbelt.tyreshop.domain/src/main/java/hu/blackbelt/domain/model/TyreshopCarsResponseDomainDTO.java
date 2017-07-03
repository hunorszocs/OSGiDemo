package hu.blackbelt.domain.model;

import hu.blackbelt.core.persistence.entity.Car;
import lombok.Data;

import java.util.List;

@Data
public class TyreshopCarsResponseDomainDTO {
    private List<Car> cars;
}
