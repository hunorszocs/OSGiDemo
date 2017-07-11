package hu.blackbelt.rest.api;

import hu.blackbelt.rest.model.TyreshopCarsResponseRestDTO;
import hu.blackbelt.rest.model.TyreshopPrintRequestRestDTO;
import hu.blackbelt.rest.model.TyreshopPrintResponseRestDTO;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/tyreshop")
public interface TyreshopRestService {

    @POST
    @Path("/print")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    String print(TyreshopPrintRequestRestDTO request);

    @GET
    @Path("/getCars")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    TyreshopCarsResponseRestDTO getCars();
}
