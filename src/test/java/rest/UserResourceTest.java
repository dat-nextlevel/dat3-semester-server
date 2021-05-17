package rest;

import com.google.gson.Gson;
import dtos.HobbyDTO;
import dtos.user.PrivateUserDTO;
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

public class UserResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();

        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            //Delete existing users and roles to get a "fresh" database
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

    //This is how we hold on to the token after login, similar to that a client must store the token somewhere
    private static String securityToken;

    //Utility method to login and set the returned securityToken
    private static void login(String username, String password) {
        String json = String.format("{username: \"%s\", password: \"%s\"}", username, password);
        securityToken = given()
                .contentType("application/json")
                .body(json)
                //.when().post("/api/login")
                .when().post("authentication/login")
                .then()
                .extract().path("token");
        //System.out.println("TOKEN ---> " + securityToken);
    }

    private void logOut() {
        securityToken = null;
    }

    // This test takes the assumption of a update user request.
    @Test
    public void updateUser() {
        PrivateUserDTO userPrivate = PrivateUserDTO.builder()
                .username("user")
                .addressId("d5325435-2cfa-44bb-86f3-be79a481e552")
                .hobbies(Arrays.asList(
                        new HobbyDTO("Anime", ""),
                        new HobbyDTO("Risk", ""),
                        new HobbyDTO("CS:GO", "") // This was changed from (Streaming)
                ))
                .radius(5)
                .build();

        login("user", "test");

        // See who I am (from me resource).
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/me").then()
                .statusCode(200)
                .body("hobbies", hasItem(hasEntry("name", "Streaming")));

        // Update me.
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .body(new Gson().toJson(userPrivate))
                .put("/user").then()
                .statusCode(200)
                .body("hobbies", hasItem(hasEntry("name", "CS:GO")));

        // See who I am again (from me resource).
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/me").then()
                .statusCode(200)
                .body("hobbies", hasItem(hasEntry("name", "CS:GO")));

    }

}
