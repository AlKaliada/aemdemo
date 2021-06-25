package com.exadel.kaliada.core.services;

import java.util.Map;

public interface HarvardSingleNewsPageCreator {
    void createPage(String pageName, Map<String, String> pagePropertyToValue);
    void deleteNewsDuplicates(Map<String, String> newsIdToLink);
}
