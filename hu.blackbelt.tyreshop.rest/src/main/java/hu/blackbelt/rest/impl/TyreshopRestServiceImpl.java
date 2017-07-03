package hu.blackbelt.rest.impl;

import hu.blackbelt.domain.api.TyreshopDomainService;
import hu.blackbelt.domain.model.TyreshopCarsResponseDomainDTO;
import hu.blackbelt.domain.model.TyreshopPrintRequestDomainDTO;
import hu.blackbelt.domain.model.TyreshopPrintResponseDomainDTO;
import hu.blackbelt.core.orika.MapperService;
import hu.blackbelt.rest.api.TyreshopRestService;
import hu.blackbelt.rest.model.TyreshopCarsResponseRestDTO;
import hu.blackbelt.rest.model.TyreshopPrintRequestRestDTO;
import hu.blackbelt.rest.model.TyreshopPrintResponseRestDTO;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, service = TyreshopRestService.class)
public class TyreshopRestServiceImpl implements TyreshopRestService {

    @Reference
    private TyreshopDomainService demoDomainService;

    @Reference
    private MapperService mapperService;

    @Activate
    public void activate() {
        mapperService.registerMap(TyreshopPrintRequestRestDTO.class, TyreshopPrintRequestDomainDTO.class);
        mapperService.registerMap(TyreshopPrintResponseRestDTO.class, TyreshopPrintResponseDomainDTO.class);
        System.err.println("TyreshopRestService.activated");
    }

    @Deactivate
    public void deactivate() {
        System.err.println("TyreshopRestService.deactivate");
    }

    @Override
    public TyreshopPrintResponseRestDTO print(TyreshopPrintRequestRestDTO request) {
        TyreshopPrintRequestDomainDTO demoPrintRequestPersistenceDTO = mapperService.getMapper().map(request, TyreshopPrintRequestDomainDTO.class);
        TyreshopPrintResponseDomainDTO result = demoDomainService.print(demoPrintRequestPersistenceDTO);

        return mapperService.getMapper().map(result, TyreshopPrintResponseRestDTO.class);
    }

    @Override
    public TyreshopCarsResponseRestDTO getCars() {
        TyreshopCarsResponseDomainDTO result = demoDomainService.getCars();

        return mapperService.getMapper().map(result, TyreshopCarsResponseRestDTO.class);
    }
}
