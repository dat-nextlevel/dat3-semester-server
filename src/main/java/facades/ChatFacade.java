package facades;

import dtos.ChatDTO;
import entities.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

public class ChatFacade {

    private static EntityManagerFactory emf;
    private static ChatFacade instance;

    private ChatFacade() {

    }

    /**
     * @param _emf
     * @return
     */

    public static ChatFacade getChatFacade(EntityManagerFactory _emf){
        if (instance == null){
            emf = _emf;
            instance = new ChatFacade();
        }
        return instance;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<ChatDTO> getChats(String username){
        EntityManager em = emf.createEntityManager();

        try{
            TypedQuery<User> q = em.createQuery("SELECT u from User u WHERE u.username = :username", User.class);
            q.setParameter("username", username);
            User user = q.getSingleResult();

            List<ChatDTO> chats = null;
        }
    }
}