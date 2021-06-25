package com.exadel.kaliada.core.services;

import java.util.Map;

public interface HarvardNewsParser {
    Map<String, String> getAllNews();
    Map<String, String> parseSingleNews(String link);
}
