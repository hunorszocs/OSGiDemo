package hu.blackbelt.persistence.impl;

import hu.blackbelt.core.persistence.api.CommonPersistenceService;
import hu.blackbelt.core.persistence.entity.Car;
import hu.blackbelt.persistence.api.TyreshopPersistenceService;
import hu.blackbelt.persistence.model.TyreshopCarsResponsePersistenceDTO;
import hu.blackbelt.persistence.model.TyreshopPrintRequestPersistenceDTO;
import hu.blackbelt.persistence.model.TyreshopPrintResponsePersistenceDTO;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import java.util.List;

@Component(immediate = true, service = TyreshopPersistenceService.class)
public class TyreshopPersistenceServiceImpl implements TyreshopPersistenceService {

    @Reference
    CommonPersistenceService commonPersistenceService;

    @Activate
    public void activate() {
        System.err.println("TyreshopPersistenceService.activated");
    }

    @Deactivate
    public void deactivate() {
        System.err.println("TyreshopPersistenceService.deactivate");
    }

    @Override
    public TyreshopPrintResponsePersistenceDTO print(TyreshopPrintRequestPersistenceDTO request) {
        TyreshopPrintResponsePersistenceDTO response = new TyreshopPrintResponsePersistenceDTO();
        response.setErrorMessage("ERROR");

        return response;
    }

    @Override
    public TyreshopCarsResponsePersistenceDTO getCars() {
        TyreshopCarsResponsePersistenceDTO response = new TyreshopCarsResponsePersistenceDTO();

        List<Car> cars = commonPersistenceService.listAll(Car.class);
        response.setCars(cars);

        return response;
    }
}
