package hu.blackbelt.rest.model;

import hu.blackbelt.core.persistence.entity.Car;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TyreshopCarsResponseRestDTO {
    @XmlElement
    private List<Car> cars;
}
