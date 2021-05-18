package facades;

import dtos.chat.ChatDTO;
import dtos.chat.MessageDTO;
import entities.User;
import entities.chat.Chat;
import entities.chat.Message;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
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
            TypedQuery<Chat> q = em.createQuery("SELECT c from Chat c WHERE c.user1.username = :username OR c.user2.username = :username", Chat.class);
            q.setParameter("username", username);
            return q.getResultList().stream().map(ChatDTO::new).collect(Collectors.toList());

        } finally {
            em.close();
        }
    }
}