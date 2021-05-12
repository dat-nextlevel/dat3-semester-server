package utils;

import entities.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest {


//Uncomment the line below, to temporarily disable this test
//@Disabled

    User u1 = new User("test1", "test_123");
    User u2 = new User("test2", "test_123");



    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the code below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {
        u1.setX("52");
        u1.setY("12");
        u2.setX("53");
        u2.setY("13");
    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    @Test
    public void testCalculateDistance(){
        assertEquals(130, Math.round(CoordinatesCalculator.calcDistance(u1,u2)));
    }

    @Test
    public void testCalculateDistanceWithRadiusTrue(){
        assertTrue(CoordinatesCalculator.calcDistanceWithRadius(u1, u2, 135));
    }

    @Test
    public void testCalculateDistanceWithRadiusFalse(){
        assertFalse(CoordinatesCalculator.calcDistanceWithRadius(u1, u2, 100));
    }
}
