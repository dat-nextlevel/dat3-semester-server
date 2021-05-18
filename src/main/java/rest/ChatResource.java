package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dtos.chat.ChatDTO;
import dtos.chat.MessageDTO;
import entities.chat.Chat;
import errorhandling.API_Exception;
import facades.ChatFacade;
import utils.EMF_Creator;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
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

    private static final ChatFacade CHAT_FACADE = ChatFacade.getChatFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public ChatResource(){}

    @GET
    public Response getChats(){
        List<ChatDTO> chats = CHAT_FACADE.getChats(securityContext.getUserPrincipal().getName());
        return Response.ok(GSON.toJson(chats)).build();
    }

    @GET
    @Path("/{username}")
    public Response getChat(@PathParam("username") String username){
        ChatDTO chat = CHAT_FACADE.getChat(securityContext.getUserPrincipal().getName(), username);
        return Response.ok(GSON.toJson(chat)).build();
    }

    @POST
    @Path("/{username}")
    public Response addMessage(@PathParam("username") String username, String jsonBody){
        String content;
        try {
            JsonObject json = JsonParser.parseString(jsonBody).getAsJsonObject();
            content = json.get("content").getAsString();
        } catch(Exception e) {
            throw new WebApplicationException("Malformed JSON Suplied",400);
        }
        MessageDTO mDTO = CHAT_FACADE.addMessage(securityContext.getUserPrincipal().getName(), username, content);
        return Response.ok(GSON.toJson(mDTO)).build();
    }
}
