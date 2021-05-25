package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.user.PrivateUserDTO;
import facades.UserFacade;
import utils.EMF_Creator;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    @Context
    SecurityContext securityContext;

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final UserFacade USER_FACADE = UserFacade.getUserFacade(EMF);

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public UserResource() {}

    @PUT
    @RolesAllowed("user")
    public Response updateUser(String json) {
        PrivateUserDTO updatedUser = GSON.fromJson(json, PrivateUserDTO.class);
        // Ensure that we only update our signed in user.
        updatedUser.setUsername(securityContext.getUserPrincipal().getName());

        updatedUser = USER_FACADE.updateUser(updatedUser);
        return Response.ok(GSON.toJson(updatedUser)).build();
    }

    @GET
    public Response getUsers() {
        return Response.ok(GSON.toJson(USER_FACADE.getUsers())).build();
    }
}
