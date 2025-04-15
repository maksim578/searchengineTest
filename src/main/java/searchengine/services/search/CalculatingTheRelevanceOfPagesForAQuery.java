package searchengine.services.search;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CalculatingTheRelevanceOfPagesForAQuery {          //TODO Разработать более точный алгоритм определение страницы (чтобы уменьшить входной параметр).
                                                                //TODO Протестить в разных сценариях.
    public void getRelevanceForAQuery(Map<Integer, List<Integer>> lemmaIdsAndPagesIds){
        if (lemmaIdsAndPagesIds.isEmpty()) return;

        List<Integer> candidatePages = lemmaIdsAndPagesIds.entrySet().iterator().next().getValue();
        int totalKeys = lemmaIdsAndPagesIds.size();
        double threshold = totalKeys * 0.65;

        List<Integer> relevantPages = new ArrayList<>();

        for(Integer candidatePage : candidatePages){
            int matchCount = 0;

            for (List<Integer> pages : lemmaIdsAndPagesIds.values()){
                if (pages.contains(candidatePage)){
                    matchCount ++;
                }

                if (matchCount >= threshold){
                    relevantPages.add(candidatePage);
                }
            }
        }

        List<Integer> result = refineTheResult(relevantPages);

        System.out.println("Страница: " + result);                                  //TODO Удалить.
    }

    private List<Integer> refineTheResult(List<Integer> inaccurateResult){

        Map<Integer, Long> frequencyMap = inaccurateResult.stream()
                .collect(Collectors.groupingBy(
                        n -> n,
                        Collectors.counting()
                ));

        Optional<Integer> mostFrequencyNumber = frequencyMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);

        return mostFrequencyNumber
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
    }
}
