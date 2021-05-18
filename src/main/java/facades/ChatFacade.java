package facades;

import dtos.chat.ChatDTO;
import dtos.chat.MessageDTO;
import entities.User;
import entities.chat.Chat;
import entities.chat.Message;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;
import java.util.List;
import java.util.stream.Collectors;

public class ChatFacade {

    private static EntityManagerFactory emf;
    private static ChatFacade instance;

    private ChatFacade() {

    }

    /**
     * @param _emf
     * @return
     */

    public static ChatFacade getChatFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new ChatFacade();
        }
        return instance;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<ChatDTO> getChats(String username) {
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Chat> q = em.createQuery("SELECT c from Chat c WHERE c.user1.username = :username OR c.user2.username = :username", Chat.class);
            q.setParameter("username", username);
            return q.getResultList().stream().map(ChatDTO::new).collect(Collectors.toList());

        } finally {
            em.close();
        }
    }

    public ChatDTO getChat(String usernameMe, String usernameOther) {
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Chat> q = em.createQuery("SELECT c FROM Chat c " +
                    "WHERE (c.user1.username = :usernameMe AND c.user2.username = :usernameOther) " +
                    "OR (c.user1.username = :usernameOther AND c.user2.username = :usernameMe)", Chat.class);
            q.setParameter("usernameMe", usernameMe);
            q.setParameter("usernameOther", usernameOther);

            return new ChatDTO(q.getSingleResult());
        } catch (NoResultException e) {
            try {
                TypedQuery<User> q = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
                q.setParameter("username", usernameMe);
                TypedQuery<User> q2 = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
                q.setParameter("username", usernameOther);
                Chat chat = new Chat(q.getSingleResult(), q2.getSingleResult());
                return new ChatDTO(chat);
            } catch (NoResultException e) {
                throw new WebApplicationException("One of the users do not exist", 500);
            }
        } finally {
            em.close();
        }
    }
}