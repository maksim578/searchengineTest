package searchengine.services.search;

import org.springframework.stereotype.Service;
import searchengine.services.indexing.LemmaAnalyzerService;

import java.util.Map;

@Service
public class SortedLemmas {    //TODO переименовать имя класса.
    private final LemmaAnalyzerService lemmaAnalyzerService;
    private final FrequencyByLemmaName frequencyByLemmaName;
    private final PageSearchForARareLemma pageSearchForARareLemma;

    public SortedLemmas(LemmaAnalyzerService lemmaAnalyzerService,
                        FrequencyByLemmaName frequencyByLemmaName,
                        PageSearchForARareLemma pageSearchForARareLemma) {
        this.lemmaAnalyzerService = lemmaAnalyzerService;
        this.frequencyByLemmaName = frequencyByLemmaName;
        this.pageSearchForARareLemma = pageSearchForARareLemma;
    }

    public void sortingLemmasByFrequency(String textFromApi, String siteUrl){      //TODO переименовать имя метода.

        Map<String, Integer> lemmasFromText = lemmaAnalyzerService.getLemmas(textFromApi);
        String[] sortedLemmasArr = lemmasFromText.keySet().toArray(new String[0]);

        Map<String, Integer> filteredFrequencyAndLemmas = frequencyByLemmaName.getFrequencyAndLemma(sortedLemmasArr, siteUrl);
        pageSearchForARareLemma.GetIdsLemmaAndIdsPageByLemma(filteredFrequencyAndLemmas);
    }
}
