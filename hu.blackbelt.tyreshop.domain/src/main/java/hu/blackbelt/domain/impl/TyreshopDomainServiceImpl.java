package hu.blackbelt.domain.impl;

import hu.blackbelt.domain.api.TyreshopDomainService;
import hu.blackbelt.domain.model.TyreshopCarsResponseDomainDTO;
import hu.blackbelt.domain.model.TyreshopPrintRequestDomainDTO;
import hu.blackbelt.domain.model.TyreshopPrintResponseDomainDTO;
import hu.blackbelt.core.orika.MapperService;
import hu.blackbelt.persistence.api.TyreshopPersistenceService;
import hu.blackbelt.persistence.model.TyreshopCarsResponsePersistenceDTO;
import hu.blackbelt.persistence.model.TyreshopPrintRequestPersistenceDTO;
import hu.blackbelt.persistence.model.TyreshopPrintResponsePersistenceDTO;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, service = TyreshopDomainService.class)
public class TyreshopDomainServiceImpl implements TyreshopDomainService {

    @Reference
    private TyreshopPersistenceService persistenceService;

    @Reference
    private MapperService mapperService;

    @Activate
    public void activate() {
        mapperService.registerMap(TyreshopPrintRequestDomainDTO.class, TyreshopPrintRequestPersistenceDTO.class);
        mapperService.registerMap(TyreshopPrintResponseDomainDTO.class, TyreshopPrintResponsePersistenceDTO.class);
        mapperService.registerMap(TyreshopCarsResponseDomainDTO.class, TyreshopCarsResponsePersistenceDTO.class);
        System.err.println("TyreshopDomainService.activated");
    }

    @Deactivate
    public void deactivate() {
        System.err.println("TyreshopDomainService.deactivate");
    }

    @Override
    public TyreshopPrintResponseDomainDTO print(TyreshopPrintRequestDomainDTO request) {
        TyreshopPrintRequestPersistenceDTO demoPrintRequestPersistenceDTO = mapperService.getMapper().map(request, TyreshopPrintRequestPersistenceDTO.class);
        TyreshopPrintResponsePersistenceDTO result = persistenceService.print(demoPrintRequestPersistenceDTO);

        return mapperService.getMapper().map(result, TyreshopPrintResponseDomainDTO.class);
    }

    @Override
    public TyreshopCarsResponseDomainDTO getCars() {
        TyreshopCarsResponsePersistenceDTO result = persistenceService.getCars();

        return mapperService.getMapper().map(result, TyreshopCarsResponseDomainDTO.class);
    }

}
