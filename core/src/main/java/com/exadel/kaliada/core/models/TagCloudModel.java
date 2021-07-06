package com.exadel.kaliada.core.models;

import com.exadel.kaliada.core.services.HarvardNewsSearchService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;

import java.util.Map;

@Model(adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TagCloudModel {

    @Self
    SlingHttpServletRequest request;

    @OSGiService
    private HarvardNewsSearchService harvardNewsSearchService;

    public Map<String, String> getTags() {
        return harvardNewsSearchService.getAllTags(request);
    }
}
