/*
 * Md. Momin Al Aziz momin.aziz.cse @ gmail.com	
 * http://www.mominalaziz.com
 */
package Database;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author shad942
 */
public class QueryDB {

    public List<Snps> getFromSnip(String snip) {
        List<Snps> res = new ArrayList<>();
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.umanitoba_smc_genome_clients_jar_1.0PU");
            EntityManager em = emf.createEntityManager();
            res = em.createNamedQuery("Snps.findBySnip", Snps.class).setParameter("snip", snip).getResultList();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }
    public List<Snps> getFromSnip(String snip,String type) {
        List<Snps> res = new ArrayList<>();
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.umanitoba_smc_genome_clients_jar_1.0PU");
            EntityManager em = emf.createEntityManager();
            res = em.createNamedQuery("Snps.findBySnipAndType", Snps.class)
                    .setParameter("snip", snip)
                    .setParameter("type", type)
                    .getResultList();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }
}
