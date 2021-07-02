package com.exadel.kaliada.core.models;

import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import java.util.List;

@Getter
@Setter
@Model(adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NewsModel {

    private String reference;

    private String image;

    private String title;

    private List<String> tags;

    private String text;

    private long likes;

    private long dislikes;
}
