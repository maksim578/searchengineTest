package searchengine.services.indexing;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import searchengine.repositories.PageRepository;
import searchengine.services.impl.LemmaIndexService;

import java.util.*;

@Service
public class SaveLemmasAndIndexInBD implements LemmaIndexService {
    private final PageRepository pageRepository;
    private final JdbcTemplate jdbcTemplate;

    public SaveLemmasAndIndexInBD(
            PageRepository pageRepository,
            JdbcTemplate jdbcTemplate
    ) {
        this.pageRepository = pageRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void saveLemmasAndIndex(Integer pageId, Integer siteId, Map<String, Integer> lemmas) {
        if (lemmas.isEmpty()) return;

        int totalPages = pageRepository.countBySiteId(siteId);

        List<Object[]> lemmaParams = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
            lemmaParams.add(new Object[]{entry.getKey(), siteId, 1, totalPages});
        }

        batchInsertLemmas(lemmaParams);

        Map<String, Integer> lemmaIdMap = fetchLemmaIds(lemmas.keySet(), siteId);

        List<Object[]> indexParams = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
            Integer lemmaId = lemmaIdMap.get(entry.getKey());
            if (lemmaId != null) {
                indexParams.add(new Object[]{lemmaId, pageId, entry.getValue().floatValue()});
            }
        }

        batchInsertIndexes(indexParams);
    }

    private void batchInsertLemmas(List<Object[]> batchArgs) {
        String sql = """
                INSERT INTO lemma (lemma, site_id, frequency)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE frequency = LEAST(frequency + 1, ?)
                """;

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private Map<String, Integer> fetchLemmaIds(Set<String> lemmaTexts, int siteId) {
        if (lemmaTexts.isEmpty()) return Collections.emptyMap();

        String inSql = String.join(",", Collections.nCopies(lemmaTexts.size(), "?"));
        String sql = "SELECT id, lemma FROM lemma WHERE site_id = ? AND lemma IN (" + inSql + ")";

        List<Object> params = new ArrayList<>();
        params.add(siteId);
        params.addAll(lemmaTexts);

        return jdbcTemplate.query(sql, params.toArray(), rs -> {
            Map<String, Integer> map = new HashMap<>();
            while (rs.next()) {
                map.put(rs.getString("lemma"), rs.getInt("id"));
            }
            return map;
        });
    }

    private void batchInsertIndexes(List<Object[]> batchArgs) {
        String sql = """
                INSERT INTO index_data (lemma_id, page_id, `rank`)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE `rank` = VALUES(`rank`)
                """;

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}
