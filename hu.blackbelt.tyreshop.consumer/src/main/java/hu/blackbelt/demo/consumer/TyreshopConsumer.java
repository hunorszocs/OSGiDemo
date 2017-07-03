package hu.blackbelt.demo.consumer;

import hu.blackbelt.rest.api.TyreshopRestService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true)
public class TyreshopConsumer {
    @Reference
    private TyreshopRestService restService;

    @Activate
    public void activate() {
        System.err.printf("TyreshopConsumer.activated (%s)\n", restService.getCars().toString());
    }

    @Deactivate
    public void deactivate() {
        System.err.println("TyreshopConsumer.deactivate");
    }
}
