package com.exadel.kaliada.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LikeModel {

    @ValueMapValue
    private long likeCounter;

    @ValueMapValue
    private long dislikeCounter;

    public long getLikeCounter() {
        return likeCounter;
    }

    public long getDislikeCounter() {
        return dislikeCounter;
    }
}
