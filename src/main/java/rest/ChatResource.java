package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.chat.MessageDTO;
import utils.EMF_Creator;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

@Path("chat")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
public class ChatResource {
    @Context
    SecurityContext securityContext;

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public ChatResource(){}

    @GET
    public Response getChats(){
        List<MessageDTO> chats = null;
        return null;
    }
}
