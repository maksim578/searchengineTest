package searchengine.models;

import lombok.Data;

@Data
public class ProcessedPageResult {

    private final String content;
    private final Integer id;
}
