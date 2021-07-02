package com.exadel.kaliada.core.services;

import com.exadel.kaliada.core.models.NewsModel;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.List;
import java.util.Map;

public interface HarvardNewsSearchService {

    List<NewsModel> getAllNews(SlingHttpServletRequest request, int offset, int limit, String tag, String locale);
    Map<String, String> getAllTags(SlingHttpServletRequest request);
}
