package facades;

import dtos.HobbyDTO;
import dtos.user.PrivateUserDTO;
import dtos.user.UserDTO;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserFacadeTest {
    private static EntityManagerFactory emf;
    private static UserFacade userFacade;
    private static HobbyFacade hobbyFacade;
    public UserFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        userFacade = UserFacade.getUserFacade(emf);
        hobbyFacade = HobbyFacade.getInstance(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the code below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            //Delete existing database data.
            em.createQuery("delete from Chat").executeUpdate();
            em.createQuery("delete from Message").executeUpdate();
            em.createQuery("delete from Hobby").executeUpdate();
            em.createQuery("delete from User").executeUpdate();
            em.createQuery("delete from Role").executeUpdate();
            em.getTransaction().commit();

            List<HobbyDTO> hobbies = Arrays.asList(
                    new HobbyDTO("CS:GO", "Gaming"),
                    new HobbyDTO("Valorant", "Gaming"),
                    new HobbyDTO("Football", "Sport"),
                    new HobbyDTO("Tennis", "Sport"),
                    new HobbyDTO("Volleyball", "Sport"),
                    new HobbyDTO("Streaming", "Entertainment"),
                    new HobbyDTO("Coding", "Technology"),
                    new HobbyDTO("Risk", "Board Game"),
                    new HobbyDTO("Dungeon And Dragons", "Board Game"),
                    new HobbyDTO("Anime", "Entertainment")
            );
            hobbyFacade.create(hobbies);

            userFacade.create("test1", "test123", new ArrayList<>());
            userFacade.create("test2", "test123", Collections.singletonList("admin"));
            userFacade.create("test3", "test123", new ArrayList<>());

            userFacade.attachHobby("test1", "Anime");
            userFacade.attachHobby("test1", "Risk");
            userFacade.attachHobby("test1", "Streaming");
            userFacade.attachHobby("test2", "Anime");
            userFacade.attachHobby("test2", "Risk");
            userFacade.attachHobby("test2", "Coding");
            userFacade.attachHobby("test3", "Anime");
            userFacade.attachHobby("test3", "Football");

            userFacade.setCoordinates("test1", "55.701929822699285", "12.532702091549336");
            userFacade.setCoordinates("test2", "55.703651686963845", "12.529579781180544");
            userFacade.setCoordinates("test3", "56.034870551225235", "12.595789041181456");
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    //@Test
    void create() {
    }

    //@Test
    void getUsers() {
    }

    //@Test
    void getMe() {
    }

    //@Test
    void attachHobby() {
    }

    @Test
    void getUsersByHobby() {
        assertEquals(3, userFacade.getUsersByHobby("Anime").size());
        assertEquals(2, userFacade.getUsersByHobby("Risk").size());
        assertEquals("test3", userFacade.getUsersByHobby("Football").get(0).getUsername());
    }

    @Test
    void getUsersWithinDistance() {
        List<UserDTO> users = userFacade.getUsersByHobby("Anime");
        PrivateUserDTO meDTO = userFacade.getPrivateUser("test1");
        assertEquals("test2", userFacade.getUsersWithinDistance(meDTO.getUsername(), users, 5).get(0).getUsername());
    }
}