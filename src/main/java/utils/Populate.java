package utils;


import com.google.common.base.Strings;
import dtos.HobbyDTO;
import entities.Hobby;
import facades.HobbyFacade;
import facades.UserFacade;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Populate {
    private final EntityManagerFactory emf;


    public static void main(String[] args) {
        new Populate(EMF_Creator.createEntityManagerFactory()).populateAll();
    }

    public Populate(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public List<String> populateAll() {
        List<String> populated = new ArrayList<>();
        if(populateHobbies())
            populated.add("hobbies");
        if(populateUsers())
            populated.add("users");

        return populated;
    }

    /**
     *
     * @return Boolean regarding table being populated or not.
     *
     * */
    public boolean populateUsers() throws IllegalArgumentException {
        UserFacade userFacade = UserFacade.getUserFacade(this.emf);

        if (!userFacade.getUsers().isEmpty()) return false;

        // NOTICE: Always set your password as environment variables.
        String password_admin = "test";
        String password_user = "test";

        boolean isDeployed = System.getenv("DEPLOYED") != null;
        if(isDeployed) {
            password_user = System.getenv("PASSWORD_DEFAULT_USER");
            password_admin = System.getenv("PASSWORD_DEFAULT_ADMIN");

            // Do not allow "empty" passwords in production.
            if(Strings.isNullOrEmpty(password_admin) || password_admin.trim().length() < 3 || Strings.isNullOrEmpty(password_user) || password_user.trim().length() < 3)
                throw new IllegalArgumentException("FAILED POPULATE OF USERS: Passwords were empty or less than 3 characters? Are environment variables: [PASSWORD_DEFAULT_USER, PASSWORD_DEFAULT_ADMIN] set?");
        }

        userFacade.create("user", password_user, new ArrayList<>());
        userFacade.create("admin", password_admin, Collections.singletonList("admin"));
        
        userFacade.setCoordinates("user", "55.701929822699285", "12.532702091549336");
        userFacade.setCoordinates("admin", "55.703651686963845", "12.529579781180544");
        
        userFacade.setRadius("user", 5);
        userFacade.setRadius("admin", 5);
        
        userFacade.attachHobby("user", "Anime");
        userFacade.attachHobby("user", "Risk");
        userFacade.attachHobby("user", "Streaming");
        userFacade.attachHobby("admin", "Anime");
        userFacade.attachHobby("admin", "Risk");
        userFacade.attachHobby("admin", "Coding");

        
        

        return true;
    }

    private boolean populateHobbies() {
        HobbyFacade hobbyFacade = HobbyFacade.getInstance(this.emf);

        if (!hobbyFacade.getHobbies().isEmpty()) return false;

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
        return true;
    }

}
