package facades;

import dtos.MeDTO;
import dtos.ProfileDTO;
import entities.RenameMe;
import entities.User;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;

/**
 *
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class ProfileFacade {

    private static ProfileFacade instance;
    private static EntityManagerFactory emf;
    
    //Private Constructor to ensure Singleton
    private ProfileFacade() {}
    
    
    /**
     * 
     * @param _emf
     * @return an instance of this facade class.
     */
    public static ProfileFacade getProfileFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new ProfileFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    //TODO Remove/Change this before use
    public ProfileDTO getProfile(String username) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<User> q = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
            q.setParameter("username", username);
            return new ProfileDTO(q.getSingleResult());
        }
        catch(NoResultException e) {
            throw new WebApplicationException("No user found with username" + username, 404);
        } finally {
            em.close();
        }
    }

}
