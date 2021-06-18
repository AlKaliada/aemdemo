package com.exadel.kaliada.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = SlingHttpServletRequest.class,
        resourceType = LikeModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.REQUIRED)
public class LikeModel {
    protected static final String RESOURCE_TYPE = "aemdemo/components/like";

    @ValueMapValue
    @Default(intValues = 0)
    private Long likeCounter;

    @ValueMapValue
    @Default(intValues = 0)
    private Long dislikeCounter;

    public Long getLikeCounter(){
        return likeCounter;
    }

    public Long getDislikeCounter(){
        return dislikeCounter;
    }
}
