package com.exadel.kaliada.core.services;

import com.exadel.kaliada.core.models.NewsModel;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * Class to search info from harvard news pages
 */

public interface HarvardNewsSearchService {

    /**
     *
     * @param request SlingHttpServletRequest
     * @param offset how many news need to skip
     * @param limit how many news need to show
     * @param tag tag name to search news by tag
     * @param locale locale name to search news by locale
     * @return result of searching news
     */

    List<NewsModel> getAllNews(SlingHttpServletRequest request, int offset, int limit, String tag, String locale);

    /**
     *
     * @param request SlingHttpServletRequest
     * @return map with keys tags and values with URL to news feed page and key tag as a parameter
     */

    Map<String, String> getAllTags(SlingHttpServletRequest request);
}
