package facades;

import dtos.user.MatchDTO;
import entities.Hobby;
import entities.User;
import utils.CoordinatesCalculator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MatchFacade {

    private static EntityManagerFactory emf;
    private static MatchFacade instance;
    private static UserFacade USER_FACADE;

    private MatchFacade() {

    }


    /**
     * @param _emf
     * @return the instance of this facade.
     */

    public static MatchFacade getMatchFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            USER_FACADE = UserFacade.getUserFacade(emf);
            instance = new MatchFacade();
        }
        return instance;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<MatchDTO> getMatches(String username) {

        User user = USER_FACADE.getUser(username);
        if(user == null) throw new WebApplicationException("No user found with username" + username, 404);

        return getMatches(username, user.getRadius(), user.getHobbies().stream().map(Hobby::getName).collect(Collectors.toList()));
    }

    public List<MatchDTO> getMatches(String username, int radius, List<String> hobbies) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<User> q = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
            q.setParameter("username", username);
            User user = q.getSingleResult();

            TypedQuery<User> q2 = em.createQuery("SELECT u FROM User u WHERE u.username != :username", User.class);
            q2.setParameter("username", username);
            List<User> users = q2.getResultList();
            List<User> matchedUsers = new ArrayList<>(users);

            for (User matchedUser : users) {
                if (matchedUser.getHobbies() == null || matchedUser.getHobbies().isEmpty() || matchedUser.getLatitude() == null || matchedUser.getLongitude() == null) {
                    matchedUsers.remove(matchedUser);
                }
                else if (Collections.disjoint(hobbies, matchedUser.getHobbies().stream().map(Hobby::getName).collect(Collectors.toList()))) {
                    matchedUsers.remove(matchedUser);
                }
                else if (!CoordinatesCalculator.calcDistanceWithRadius(user, matchedUser, radius)) {
                    matchedUsers.remove(matchedUser);
                }
            }

            return matchedUsers.stream().map(_user -> new MatchDTO(_user, CoordinatesCalculator.calcDistance(user, _user))).collect(Collectors.toList());

        } catch (NoResultException e) {
            throw new WebApplicationException("No user found with username" + username, 404);
        }
        finally {
            em.close();
        }
    }
}
