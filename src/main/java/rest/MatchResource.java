package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.Hobby;
import facades.HobbyFacade;
import facades.MatchFacade;
import utils.EMF_Creator;
import facades.FacadeExample;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

//Todo Remove or change relevant parts before ACTUAL use
@Path("match")
public class MatchResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
       
    private static final HobbyFacade HOBBY_FACADE =  HobbyFacade.getInstance(EMF);
    private static final MatchFacade MATCH_FACADE = MatchFacade.getMatchFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
            
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/matches")
    @RolesAllowed("user")
    public String getMatches(@PathParam("hobby") String hobby){

        return "jhe";
    }
}
