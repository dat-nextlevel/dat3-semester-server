package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.ProfileDTO;
import utils.EMF_Creator;
import facades.ProfileFacade;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//Todo Remove or change relevant parts before ACTUAL use
@Path("profile")
public class ProfileResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
       
    private static final ProfileFacade FACADE =  ProfileFacade.getProfileFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
            
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Hello World\"}";
    }
    @Path("{username}")
    @GET
    @RolesAllowed("user")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getUser(@PathParam("username") String username) {
        ProfileDTO profile = FACADE.getProfile(username);
        return Response.ok(GSON.toJson(profile)).build();
        
        //System.out.println("--------------->"+count);
    }
}
