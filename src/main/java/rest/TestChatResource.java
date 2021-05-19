package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dtos.chat.ChatDTO;
import dtos.chat.MessageDTO;
import facades.ChatFacade;
import utils.EMF_Creator;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Path("chat-test")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestChatResource {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();

    private static final ChatFacade CHAT_FACADE = ChatFacade.getChatFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Path("/{username}")
    public Response getChats(@PathParam("username") String username) {
        List<ChatDTO> chats = CHAT_FACADE.getChats(username);
        Map<String, List<ChatDTO>> data = Collections.singletonMap("data", chats);
        return Response.ok(GSON.toJson(data)).build();
    }

    @GET
    @Path("/{username1}/{username2}")
    public Response getChat(@PathParam("username1") String username1, @PathParam("username2") String username2) {
        ChatDTO chat = CHAT_FACADE.getChat(username1, username2);
        return Response.ok(GSON.toJson(chat)).build();
    }

    @POST
    @Path("/{username1}/{username2}")
    public Response addMessage(@PathParam("username1") String username1, @PathParam("username2") String username2,String jsonBody) {
        String content;
        try {
            JsonObject json = JsonParser.parseString(jsonBody).getAsJsonObject();
            content = json.get("content").getAsString();
        } catch (Exception e) {
            throw new WebApplicationException("Malformed JSON Suplied", 400);
        }
        MessageDTO mDTO = CHAT_FACADE.addMessage(username1, username2, content);
        return Response.ok(GSON.toJson(mDTO)).build();
    }
}
