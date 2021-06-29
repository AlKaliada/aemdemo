package com.exadel.kaliada.core.services;

import java.util.Map;

/**
 * Class to load news from https://news.harvard.edu/gazette/ and parse them
 * @author akaliada
 */

public interface HarvardNewsParser {

    /**
     * load and parse all news from the main web-site
     * @return Map of pairs news Id and news link
     */

    Map<String, String> getAllNews();

    /**
     * load and parse single news by link
     * @param link - link to single news
     * @return Map with pairs components of news page (tag, image, title, text) and their values
     */

    Map<String, String> parseSingleNews(String link);
}
