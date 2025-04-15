package searchengine.services.search;

import org.springframework.stereotype.Service;
import searchengine.services.indexing.LemmaAnalyzerService;

import java.util.Map;

@Service
public class GetPageByQueryWords {
    private final LemmaAnalyzerService lemmaAnalyzerService;
    private final GetFrequencyByLemmaName getFrequencyByLemmaName;
    private final PageSearchForARareLemma pageSearchForARareLemma;

    public GetPageByQueryWords(LemmaAnalyzerService lemmaAnalyzerService,
                               GetFrequencyByLemmaName getFrequencyByLemmaName,
                               PageSearchForARareLemma pageSearchForARareLemma
    ) {
        this.lemmaAnalyzerService = lemmaAnalyzerService;
        this.getFrequencyByLemmaName = getFrequencyByLemmaName;
        this.pageSearchForARareLemma = pageSearchForARareLemma;
    }

    public void getAndSortingLemmasAndFrequency(String textFromApi, String siteUrl){

        Map<String, Integer> lemmasFromText = lemmaAnalyzerService.getLemmas(textFromApi);
        String[] sortedLemmasArr = lemmasFromText.keySet().toArray(new String[0]);

        Map<String, Integer> filteredFrequencyAndLemmas = getFrequencyByLemmaName.getFrequencyAndLemma(sortedLemmasArr, siteUrl);
        pageSearchForARareLemma.GetIdsLemmaAndIdsPageByLemma(filteredFrequencyAndLemmas);
    }
}
