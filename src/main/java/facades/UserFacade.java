package facades;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.DawaDTO;
import dtos.user.PrivateUserDTO;
import dtos.user.UserDTO;
import entities.Hobby;
import entities.Role;
import entities.User;

import javax.persistence.*;
import javax.ws.rs.WebApplicationException;

import errorhandling.ValidationException;
import org.apache.commons.lang3.StringUtils;
import security.errorhandling.AuthenticationException;
import utils.CoordinatesCalculator;
import utils.HttpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;
    private final String DAWA_URL = "https://api.dataforsyningen.dk";
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private UserFacade() {
    }

    /**
     * @param _emf
     * @return the instance of this facade.
     */
    public static UserFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }

    public User getVerifiedUser(String username, String password) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            TypedQuery<User> q = em.createQuery("SELECT u from User u WHERE u.username = :username", User.class);
            q.setParameter("username", username);
            user = q.getSingleResult();

            if (user == null || !user.verifyPassword(password)) {
                throw new AuthenticationException("Invalid user name or password");
            }
        } finally {
            em.close();
        }
        return user;
    }

    public User create(String username, String password, List<String> roles) {
        EntityManager em = emf.createEntityManager();
        try {
            User user = new User();

            // Validate
            if ((Strings.isNullOrEmpty(username) || username.length() < 3) || (Strings.isNullOrEmpty(password) || password.length() < 3))
                throw new WebApplicationException("Username and/or password should be more than 3 characters!", 400);

            user.setUsername(username);
            user.setPassword(password);

            // Always want the default role for a user.
            Role defaultRole = em.find(Role.class, Role.DEFAULT_ROLE);
            if(defaultRole == null)
                defaultRole = new Role(Role.DEFAULT_ROLE);
            user.addRole(defaultRole);

            // See if role already exists in database... if not:
            // check if the "new role" exists in our enums of roles (! no dynamic roles !), else skip this role.
            roles.forEach(roleName -> {
                Role role;
                role = em.find(Role.class, roleName);
                if(role == null) {
                    String foundSystemRole = Role.findRole(roleName);
                    if(foundSystemRole != null)
                        role = new Role(foundSystemRole);
                    else return;
                }

                user.addRole(role);
            });

            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();

            return user;
        } finally {
            em.close();
        }
    }

    public User register (String username, String password, String verifyPassword){
        List<String> errors = new ArrayList<>();

        //Username Validation
        if (StringUtils.isAllBlank(username) || getUser(username) != null){
            errors.add("Username is blank or is taken");
        }

        //Password validation
        if (!password.equals(verifyPassword)){
            errors.add("Passwords to not match");
        }

        if (!errors.isEmpty()){
            throw new ValidationException("These fields have issues", errors);
        } else {
            List<String> roles = new ArrayList<>();
            return create(username, password, roles);
        }
    }

    public List<UserDTO> getUsers() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<User> q = em.createQuery("SELECT u FROM User u", User.class);
            return q.getResultList().stream().map(UserDTO::new).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public PrivateUserDTO getPrivateUser(String username) {
        User me = getUser(username);
        if(me == null) throw new WebApplicationException("No user found with username " + username, 404);

        if(me.getAddressId() == null)
            return new PrivateUserDTO(me);
        else {
            DawaDTO dawa = getDawaByAddressId(me.getAddressId());
            return new PrivateUserDTO(me, dawa);
        }
    }

    public User getUser(String username) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<User> q = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
            q.setParameter("username", username);
            return q.getSingleResult();
        }
        catch(NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public PrivateUserDTO updateUser(PrivateUserDTO updatedUser) {
        EntityManager em = emf.createEntityManager();

        try {
            // Replace all accessible entries for our user.
            User user = getUser(updatedUser.getUsername());
            if (user == null)
                throw new WebApplicationException("No user found with username " + updatedUser.getUsername(), 404);

            // Only replace posted fields (check if null first).

            if(updatedUser.getHobbies() != null) {
                user.removeAllHobbies();
                List<Hobby> hobbies = updatedUser.getHobbies().stream().map((hobby) -> em.find(Hobby.class, hobby.getName())).collect(Collectors.toList());
                user.setHobbies(hobbies);
            }

            if (updatedUser.getAddressId() != null) {
                user.setAddressId(updatedUser.getAddressId());

                // If this was updated we need to set latitude and longitude again.
                DawaDTO dawa = getDawaByAddressId(updatedUser.getAddressId());
                user.setLongitude(String.valueOf(dawa.getX()));
                user.setLatitude(String.valueOf(dawa.getY()));
            }

            if (updatedUser.getRadius() != 0)
                user.setRadius(updatedUser.getRadius());

            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();

            return getPrivateUser(user.getUsername());
        }
        finally {
            em.close();
        }
    }

    public void attachHobby(String username, String name) {
        EntityManager em = emf.createEntityManager();
        User user = getUser(username);
        try {
            Hobby query = em.find(Hobby.class, name);
            if (query != null) {
                em.getTransaction().begin();
                user.addHobby(query);
                em.merge(user);
                em.getTransaction().commit();
            }
        } finally {
            em.close();
        }
    }

    public void setCoordinates(String username, String latitude, String longitude) {
        EntityManager em = emf.createEntityManager();
        User user = getUser(username);
        try {
            em.getTransaction().begin();
            user.setLatitude(latitude);
            user.setLongitude(longitude);
            em.merge(user);
            em.getTransaction().commit();
        }
        finally {
            em.close();
        }
    }
    
    public void setRadius(String username, int radius) {
        EntityManager em = emf.createEntityManager();
        User user = getUser(username);
        try {
            em.getTransaction().begin();
            user.setRadius(radius);
            em.merge(user);
            em.getTransaction().commit();
        }
        finally {
            em.close();
        }
    }

    public List<UserDTO> getUsersByHobby(String hobby) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<User> q = em.createQuery("SELECT distinct u FROM User u JOIN u.hobbies hobbies WHERE hobbies.name = :hobby", User.class);
            q.setParameter("hobby", hobby);
            return q.getResultList().stream().map(UserDTO::new).collect(Collectors.toList());
        }
        finally {
            em.close();
        }
    }

    public List<UserDTO> getUsersWithinDistance(String username, List<UserDTO> users, int distance) {
        User me = getUser(username);
        List<UserDTO> retUsers = new ArrayList<>();
        users.removeIf(user -> user.getUsername().equals(me.getUsername()));
        for (UserDTO u : users) {
            User user = getUser(u.getUsername());
            if (CoordinatesCalculator.calcDistanceWithRadius(me, user, distance)) {
                retUsers.add(new UserDTO(user));
            }
        }
        return retUsers;
    }

    public DawaDTO getDawaByCoordinates(double x, double y){
        String path = "/adgangsadresser/reverse";
        String query = "?x=" + x + "&y=" + y;
        String url = DAWA_URL + path + query;

        return getDawaInformation(url);
    }

    public DawaDTO getDawaByAddressId(String id){
        String path = "/adresser/" + id;
        String url = DAWA_URL + path;

        return getDawaInformation(url);
    }

    private DawaDTO getDawaInformation(String apiQueryUrl) {
        // Modify to minified response.
        apiQueryUrl = apiQueryUrl + (apiQueryUrl.contains("?") ? "&struktur=mini" : "?struktur=mini");


        ExecutorService executor = Executors.newCachedThreadPool();

        // Required...
        String finalApiQueryUrl = apiQueryUrl;

        Callable<String> getDataFromApi = () -> HttpUtils.fetchData(finalApiQueryUrl);
        Future<String> future = executor.submit(getDataFromApi);

        DawaDTO dawaDTO;

        try{
            String json = future.get();
            dawaDTO = GSON.fromJson(json, DawaDTO.class);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new WebApplicationException("Error when connecting to external API", 500);
        }

        executor.shutdown();
        return dawaDTO;
    }
}