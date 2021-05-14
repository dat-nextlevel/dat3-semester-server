package facades;

import com.google.common.base.Strings;
import dtos.MeDTO;
import dtos.UserDTO;
import entities.Role;
import entities.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;

import errorhandling.ValidationException;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;
import security.errorhandling.AuthenticationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;

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

    public void register (String username, String password, String verifyPassword){
        List<String> errors = new ArrayList<>();

        //Username Validation
        if (StringUtils.isAllBlank() || getUser(username) == null){
            errors.add("Username is blank or is taken");
        }

        if (!password.equals(verifyPassword)){
            errors.add("Passwords to not match");
        }

        if (!errors.isEmpty()){
            throw new ValidationException("These fields have issues", errors);
        } else {
            List<String> roles = new ArrayList<>();
            create(username, password, roles);
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

    public MeDTO getUser(String username) {
        EntityManager em = emf.createEntityManager();
        try{
            TypedQuery<User> q = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
            q.setParameter("username", username);
            return new MeDTO(q.getSingleResult());
        } finally {
            em.close();
        }
    }

    public MeDTO getMe(String username) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<User> q = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
            q.setParameter("username", username);
            return new MeDTO(q.getSingleResult());
        }
        catch(NoResultException e) {
            throw new WebApplicationException("No user found with username" + username, 404);
        } finally {
            em.close();
        }
    }
}