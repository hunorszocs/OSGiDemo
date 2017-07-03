package hu.blackbelt.domain.api;

import hu.blackbelt.domain.model.TyreshopCarsResponseDomainDTO;
import hu.blackbelt.domain.model.TyreshopPrintRequestDomainDTO;
import hu.blackbelt.domain.model.TyreshopPrintResponseDomainDTO;

public interface TyreshopDomainService {
    TyreshopPrintResponseDomainDTO print(TyreshopPrintRequestDomainDTO message);
    TyreshopCarsResponseDomainDTO getCars();
}