package searchengine.services.search;

import org.springframework.stereotype.Service;
import searchengine.repositories.LemmaIndexRepository;
import searchengine.repositories.LemmaRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PageSearchForARareLemma {  //TODO test.

    private final CalculatingTheRelevanceOfPagesForAQuery calculatingTheRelevanceOfPagesForAQuery;
    private final LemmaIndexRepository lemmaIndexRepository;
    private final LemmaRepository lemmaRepository;

    public PageSearchForARareLemma(CalculatingTheRelevanceOfPagesForAQuery calculatingTheRelevanceOfPagesForAQuery,
                                   LemmaIndexRepository lemmaIndexRepository,
                                   LemmaRepository lemmaRepository
    ){
        this.calculatingTheRelevanceOfPagesForAQuery = calculatingTheRelevanceOfPagesForAQuery;
        this.lemmaIndexRepository = lemmaIndexRepository;
        this.lemmaRepository = lemmaRepository;
    }

    public void GetIdsLemmaAndIdsPageByLemma(Map<String, Integer> mapLemmasAndFrequency){

        List<Integer> lemmaIds = getLemmaIds(mapLemmasAndFrequency);
        Map<Integer, List<Integer>> arrPageIds = getPageIdsByLemmaName(lemmaIds);

        calculatingTheRelevanceOfPagesForAQuery.getRelevanceForAQuery(getSorteredMap(arrPageIds));
    }

    private List<Integer> getLemmaIds(Map<String, Integer> mapLemmasAndFrequency){
        if (mapLemmasAndFrequency.isEmpty()){
            System.out.println("mapLemmasAndFrequency - пустой.");      //TODO Сделать лог.
            return Collections.emptyList();
        }

        return mapLemmasAndFrequency.keySet().stream()
                .map(lemmaRepository::getIdLemmaByLemmaName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Map<Integer, List<Integer>> getPageIdsByLemmaName(List<Integer> lemmaIds){

        if (lemmaIds.isEmpty()){
            System.out.println("getPageIdsByLemmaName - пустой.");      //TODO Сделать лог.
            return Collections.emptyMap();
        }

        return lemmaIds.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        lemmaIndexRepository::getPageIdsByLemmaId
                ));
    }

    private Map<Integer, List<Integer>> getSorteredMap(Map<Integer, List<Integer>> unSorteredMap){

        return unSorteredMap.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(entry -> entry.getValue().size()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}
