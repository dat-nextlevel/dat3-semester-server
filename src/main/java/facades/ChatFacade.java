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
    private static final UserFacade USER_FACADE = UserFacade.getUserFacade(emf);

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
        return new ChatDTO(_getChat(usernameMe, usernameOther));
    }

    public MessageDTO addMessage(String usernameMe, String usernameOther, String content) {
        EntityManager em = emf.createEntityManager();

        try {
            Chat chat = _getChat(usernameMe, usernameOther);

            User author = USER_FACADE.getUser(usernameMe);
            if (author == null) {
                throw new WebApplicationException("The signed in user was not found", 500);
            }
            Message message = new Message(author, content);
            chat.addMessage(message);
            em.getTransaction().begin();
            em.merge(chat);
            em.getTransaction().commit();
            return new MessageDTO(message);
        } finally {
            em.close();
        }
    }

    private Chat _getChat(String usernameMe, String usernameOther) {
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Chat> q = em.createQuery("SELECT c FROM Chat c " +
                    "WHERE (c.user1.username = :usernameMe AND c.user2.username = :usernameOther) " +
                    "OR (c.user1.username = :usernameOther AND c.user2.username = :usernameMe)", Chat.class);
            q.setParameter("usernameMe", usernameMe);
            q.setParameter("usernameOther", usernameOther);

            return q.getSingleResult();
        } catch (NoResultException e) {
            User u1 = USER_FACADE.getUser(usernameMe);
            User u2 = USER_FACADE.getUser(usernameOther);
            if (u1 == null || u2 == null) {
                throw new WebApplicationException("One of the users do not exist", 403);
            }
            Chat chat = new Chat(u1, u2);
            em.getTransaction().begin();
            em.persist(chat);
            em.getTransaction().commit();
            return chat;
        } finally {
            em.close();
        }
    }
}