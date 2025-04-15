package searchengine.services.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import searchengine.repositories.LemmaRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GetFrequencyByLemmaName {
    private final LemmaRepository lemmaRepository;
    private final FilterLemmasBySiteName filterLemmasBySiteName;
    private final Logger logger = LoggerFactory.getLogger(GetFrequencyByLemmaName.class);

    public GetFrequencyByLemmaName(LemmaRepository lemmaRepository,
                                   FilterLemmasBySiteName filterLemmasBySiteName
    ) {
        this.lemmaRepository = lemmaRepository;
        this.filterLemmasBySiteName = filterLemmasBySiteName;
    }

    public Map<String, Integer> getFrequencyAndLemma(String[] lemmasArr, String siteUrl){

        Map<String, Integer> unsortedMap = Arrays.stream(lemmasArr)
                .map(this::getLemmasAndFrequencyFromDB)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        Integer::max
                ));

        return filterLemmasBySiteName.filterLemmas(unsortedMap, siteUrl);
    }

    private Map<String, Integer> getLemmasAndFrequencyFromDB(String lemma){

        List<Object[]> result = lemmaRepository.findMaxFrequencyByLemma(lemma);

        if (result.isEmpty()){
            logger.warn("Лемма {} не найдена.", lemma);
            return Collections.emptyMap();
        }

        Object[] firstResult = result.get(0);

        try {
            String foundLemma = (String) firstResult[0];
            int frequency = ((Number) firstResult[1]).intValue();
            return Map.of(foundLemma, frequency);
        }catch (ClassCastException | NullPointerException e){
            logger.error("Ошибка обработки результата для леммы {}: {}", lemma, e.getMessage());
        return Collections.emptyMap();
        }
    }
}
