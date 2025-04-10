package searchengine.services;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.models.Index;

public class LemmaIndexRepository extends JpaRepository<Index, Long> {
}
