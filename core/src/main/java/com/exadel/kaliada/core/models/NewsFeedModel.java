package com.exadel.kaliada.core.models;

import com.exadel.kaliada.core.services.HarvardNewsSearchService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Getter
@Model(adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NewsFeedModel {

    @Self
    SlingHttpServletRequest request;

    @OSGiService
    private HarvardNewsSearchService harvardNewsSearchService;

    @ValueMapValue
    @Default(intValues = 10)
    private int limit;

    @ValueMapValue
    private int offset;

    @ValueMapValue
    private String tag;

    @ValueMapValue
    private String locale;

    @ValueMapValue
    private List<NewsModel> news;

    public List<NewsModel> getNews() {
        news = harvardNewsSearchService.getAllNews(request, offset, limit, tag, locale);
        offset += limit; // TODO transfer to UI
        return news;
    }

    @PostConstruct
    private void init() {
        String offsetParameter = request.getParameter("offset");
        if (offsetParameter != null) {
            offset = Integer.parseInt(offsetParameter);
        }
        tag = request.getParameter("tag");
        locale = request.getRequestURI().contains("/ru/") ? "ru" : "en";
    }
}
