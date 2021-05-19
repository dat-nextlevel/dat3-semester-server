package rest;

import dtos.HobbyDTO;
import dtos.user.PrivateUserDTO;
import facades.UserFacade;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
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
import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;

public class MatchResourceTest {
    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    private static UserFacade USER_FACADE;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        USER_FACADE = UserFacade.getUserFacade(emf);

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

    @Test
    public void testMatchesInRange () {
        // We know both users (admin and user) have Anime and Risk in common from Populate();
        // We also know their distance is approximate 11 km.

        login("user", "test");

        PrivateUserDTO userPrivate = PrivateUserDTO.builder().
                username("user")
                .radius(15)
                .build();

        USER_FACADE.updateUser(userPrivate);

        // Admin should be in range with user (me) (15 km).
        given()
                .header("x-access-token", securityToken)
                .when()
                .get("/matches")
                .then()
                .statusCode(200)
                .body("body", hasSize(1));

    }
    // We know both users (admin and user) have Anime and Risk in common from Populate();
    // We also know their distance is approximate 11 km.

    @Test
    public void testMatchesShouldNotBeInRange () {

        login("user", "test");

        PrivateUserDTO userPrivate = PrivateUserDTO.builder().
                username("user")
                .radius(9)
                .build();

        USER_FACADE.updateUser(userPrivate);

        // Admin should be in range with user (me) (15 km).
        given()
                .header("x-access-token", securityToken)
                .when()
                .get("/matches")
                .then()
                .statusCode(200)
                .body("body", hasSize(0));

    }

    @Test
    public void testNoMatchingHobbies () {

        login("user", "test");

        PrivateUserDTO userPrivate = PrivateUserDTO.builder().
                username("user")
                .hobbies(Collections.singletonList(new HobbyDTO("Tennis", "")))
                .radius(15)
                .build();

        USER_FACADE.updateUser(userPrivate);

        // Admin should NOT be in range with user (me) (9 km).
        given()
                .header("x-access-token", securityToken)
                .when()
                .get("/matches")
                .then()
                .statusCode(200)
                .body("body", hasSize(0));

    }

    @Test
    public void testGlobalSearch () {

        login("user", "test");

        PrivateUserDTO userPrivate = PrivateUserDTO.builder().
                username("user")
                .hobbies(Collections.singletonList(new HobbyDTO("Tennis", "")))
                .radius(15)
                .build();

        USER_FACADE.updateUser(userPrivate);

        // Admin should be in range with user (me) (15 km).
        // But they have no matching hobbies...
        given()
                .header("x-access-token", securityToken)
                .when()
                .get("/matches")
                .then()
                .statusCode(200)
                .body("body", hasSize(0));

        // But now we perform a global search. (Including queryparams, hobbies and radius)
        given()
                .header("x-access-token", securityToken)
                .when()
                .queryParam("hobbies", "Anime", "CS:GO")
                .queryParam("radius", "15")
                .get("/matches")
                .then()
                .statusCode(200)
                .body("body", hasSize(1));



    }

}
