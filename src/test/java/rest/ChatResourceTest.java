package rest;

import com.google.gson.Gson;
import dtos.HobbyDTO;
import dtos.chat.MessageDTO;
import dtos.user.PrivateUserDTO;
import entities.chat.Message;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;
import utils.Populate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class ChatResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer(){
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUp() {
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();

        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer(){
        EMF_Creator.endREST_TestWithDB();

        httpServer.shutdownNow();
    }

    @BeforeEach
    public void setup(){
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            //Delete existing users and roles to get a "fresh" database
            em.createQuery("delete from Chat").executeUpdate();
            em.createQuery("delete from Message").executeUpdate();
            em.createQuery("delete from Hobby").executeUpdate();
            em.createQuery("delete from User").executeUpdate();
            em.createQuery("delete from Role").executeUpdate();
            //System.out.println("Saved test data to database");
            em.getTransaction().commit();

            new Populate(EMF_Creator.createEntityManagerFactoryForTest()).populateAll();
        } finally {
            em.close();
        }
    }

    private static String securityToken;

    private static void login(String username, String password){
        String json = String.format("{username: \"%s\", password: \"%s\"}", username, password);
        securityToken = given()
                .contentType("application/json")
                .body(json)
                //.when().post("/api/login")
                .when().post("authentication/login")
                .then()
                .extract().path("token");
        //System.out.println("Token ---> " + securityToken);
    }

    @Test
    void getChats() {
        login("user", "test");

        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/chat").then()
                .statusCode(200)
                .body("data", hasSize(1));

        login("admin", "test");

        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/chat").then()
                .statusCode(200)
                .body("data", hasSize(1));
    }

    @Test
    void getChat() {
        login("user", "test");

        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/chat/admin").then()
                .statusCode(200)
                .body("participants", hasSize(2));
    }

    @Test
    void addMessage() {
        login("user", "test");
        String json = String.format("{content: \"%s\"}", "This is a test message");

        given()
                .contentType("application/json")
                .body(json)
                .header("x-access-token", securityToken)
                .when()
                .post("/chat/admin").then()
                .statusCode(200);

        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/chat/admin").then()
                .statusCode(200)
                .body("messages", hasSize(2));
    }
}