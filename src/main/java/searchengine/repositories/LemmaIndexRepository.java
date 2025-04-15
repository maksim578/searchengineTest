package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.models.IndexData;

import java.util.List;

@Repository
public interface LemmaIndexRepository extends JpaRepository<IndexData, Long> {
    boolean existsByLemmaIdAndPageId(int lemmaId, int pageId);
    @Query("SELECT i.pageId FROM IndexData i WHERE lemmaId = :lemmaId")
    List<Integer> getPageIdsByLemmaId(@Param("lemmaId") int lemmaId);

}
