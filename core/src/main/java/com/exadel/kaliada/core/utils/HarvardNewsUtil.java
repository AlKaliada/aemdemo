package com.exadel.kaliada.core.utils;

import com.exadel.kaliada.core.services.HarvardNewsParser;
import com.exadel.kaliada.core.services.HarvardSingleNewsPageCreator;

import java.util.Map;

public class HarvardNewsUtil {

    private HarvardNewsUtil() {}

    public static void updateNews(HarvardNewsParser harvardNewsParser, HarvardSingleNewsPageCreator harvardSingleNewsPageCreator) {
        Map<String, String> news = harvardNewsParser.getAllNews();
        harvardSingleNewsPageCreator.deleteNewsDuplicates(news);
        for (Map.Entry<String, String> entry : news.entrySet()) {
            Map<String, String> resourceToValue = harvardNewsParser.parseSingleNews(entry.getValue());
            harvardSingleNewsPageCreator.createPage(entry.getKey(), resourceToValue);
        }
    }
}
