package facades;

import dtos.HobbyDTO;
import dtos.UserDTO;
import entities.Hobby;
import entities.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

public class HobbyFacade {
    private static EntityManagerFactory emf;
    private static HobbyFacade instance;

    private HobbyFacade() {
    }

    /**
     * @param _emf
     * @return the instance of this facade.
     */
    public static HobbyFacade getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new HobbyFacade();
        }
        return instance;
    }


    public List<HobbyDTO> getHobbies() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Hobby> q = em.createQuery("SELECT h FROM Hobby h", Hobby.class);
            return q.getResultList().stream().map(HobbyDTO::new).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public List<HobbyDTO> create(List<HobbyDTO> hobbies) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            hobbies.forEach(hobbyDTO -> {
                em.persist(new Hobby(hobbyDTO.getName(), hobbyDTO.getCategory()));
            });
            em.getTransaction().commit();

            // Just return the same list back.
            return hobbies;
        } finally {
            em.close();
        }
    }
}
