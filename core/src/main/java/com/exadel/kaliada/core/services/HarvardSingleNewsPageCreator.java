package com.exadel.kaliada.core.services;

import java.util.Map;

/**
 * Create two pages in en and ru locales
 * @author akaliada
 */

public interface HarvardSingleNewsPageCreator {

    /**
     * create two pages in en and ru locales
     * @param pageName - name of new page
     * @param pagePropertyToValue - pairs components of news page (tag, image, title, text) and their values
     */

    void createPage(String pageName, Map<String, String> pagePropertyToValue);

    /**
     * check already exists news in repo by Id and delete them from Map
     * @param newsIdToLink - pairs news Id and news link
     */

    void deleteNewsDuplicates(Map<String, String> newsIdToLink);
}
