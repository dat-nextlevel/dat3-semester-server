package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.user.MatchDTO;
import facades.MatchFacade;
import utils.EMF_Creator;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

@Path("matches")
public class MatchResource {
    @Context
    SecurityContext securityContext;
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
       
    private static final MatchFacade MATCH_FACADE = MatchFacade.getMatchFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
            
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    public Response getMatches(){
        List<MatchDTO> matches = MATCH_FACADE.getMatches(securityContext.getUserPrincipal().getName());
        return Response.ok(GSON.toJson(matches)).build();
    }
}
