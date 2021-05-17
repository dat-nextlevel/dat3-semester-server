package facades;

import dtos.user.MatchDTO;
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

    private MatchFacade() {

    }


    /**
     * @param _emf
     * @return the instance of this facade.
     */

    public static MatchFacade getMatchFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new MatchFacade();
        }
        return instance;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<MatchDTO> getMatches(String username) {

        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<User> q = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
            q.setParameter("username", username);
            User user = q.getSingleResult();

            TypedQuery<User> q2 = em.createQuery("SELECT u FROM User u WHERE u.username != :username", User.class);
            q2.setParameter("username", username);
            List<User> users = q2.getResultList();
            List<User> matchedUsers = new ArrayList<>(users);

            for (User u : users) {
                if (Collections.disjoint(user.getHobbies(), u.getHobbies())) {
                    matchedUsers.remove(u);
                }
                if (!CoordinatesCalculator.calcDistanceWithRadius(user, u, user.getRadius())) {
                    matchedUsers.remove(u);
                }
            }

            return matchedUsers.stream().map(_user -> new MatchDTO(_user, CoordinatesCalculator.calcDistance(user, _user))).collect(Collectors.toList());

        } catch (NoResultException e) {
            throw new WebApplicationException("No user found with username" + username, 404);
        }
    }
}
