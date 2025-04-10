package searchengine.config.services.impl;

import java.util.Map;

public interface LemmaIndexService {
     void saveLemmasAndIndex(Integer pageId, Integer siteId, Map<String, Integer> lemmas);
}
