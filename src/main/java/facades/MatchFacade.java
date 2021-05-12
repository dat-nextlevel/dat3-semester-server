package facades;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class MatchFacade {

    private static EntityManagerFactory emf;
    private static MatchFacade instance;

    private MatchFacade(){

    }


    /**
     * @param _emf
     * @return the instance of this facade.
     */

    public static MatchFacade getMatchFacade(EntityManagerFactory _emf){
        if (instance == null) {
            emf = _emf;
            instance = new MatchFacade();
        }
        return instance;
    }

    public EntityManager getEntityManager () {return emf.createEntityManager();}
}
