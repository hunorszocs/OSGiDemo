package hu.blackbelt.persistence.api;

import hu.blackbelt.persistence.model.TyreshopCarsResponsePersistenceDTO;
import hu.blackbelt.persistence.model.TyreshopPrintRequestPersistenceDTO;
import hu.blackbelt.persistence.model.TyreshopPrintResponsePersistenceDTO;

public interface TyreshopPersistenceService {
    TyreshopPrintResponsePersistenceDTO print(TyreshopPrintRequestPersistenceDTO request);
    TyreshopCarsResponsePersistenceDTO getCars();
}
