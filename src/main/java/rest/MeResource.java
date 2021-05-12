package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.MeDTO;
import facades.UserFacade;
import utils.EMF_Creator;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/me")
@Produces(MediaType.APPLICATION_JSON)
public class MeResource {
    @Context
    SecurityContext securityContext;

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final UserFacade USER_FACADE = UserFacade.getUserFacade(EMF);

    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public MeResource() {}

    @GET
    @RolesAllowed("user")
    public Response getMe() {
        MeDTO me = USER_FACADE.getMe(securityContext.getUserPrincipal().getName());
        return Response.ok(GSON.toJson(me)).build();
    }
}
