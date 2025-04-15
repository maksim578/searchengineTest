package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.models.Lemma;

import javax.persistence.LockModeType;
import java.util.List;

@Repository
public interface LemmaRepository extends JpaRepository<Lemma, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM Lemma l WHERE l.lemma IN :lemmas AND l.siteId = :siteId ORDER BY l.lemma")
    List<Lemma> findByLemmaListForUpdate(@Param("lemmas") List<String> lemmas, @Param("siteId") Integer siteId);
    @Query("SELECT COUNT(l) FROM Lemma l WHERE l.siteId = (SELECT s.id FROM Site s WHERE s.url = :siteUrl)")
    Integer countLemmasBySiteUrl(String siteUrl);
    @Query("SELECT l.lemma, l.frequency FROM Lemma l WHERE l.lemma = :lemma")
    List<Object[]> findMaxFrequencyByLemma(@Param("lemma") String lemma);
    @Query("SELECT l.siteId FROM Lemma l WHERE l.lemma = :lemma")
    Integer findSiteIdByLemmaName(@Param("lemma") String lemma);
    @Query("SELECT l.id FROM Lemma l WHERE l.lemma = :lemma")
    Integer getIdLemmaByLemmaName(@Param("lemma") String lemma);
}

