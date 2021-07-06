package com.exadel.kaliada.core.models;

import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.inject.Named;
import java.util.List;

@Getter
@Setter
@Model(adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NewsModel {

    @ValueMapValue
    private String reference;

    @ValueMapValue
    private String image;

    @ValueMapValue
    private String title;

    @ValueMapValue
    private List<String> tags;

    @ValueMapValue
    private String text;

    @ValueMapValue
    private long likes;

    @ValueMapValue
    private long dislikes;
}
