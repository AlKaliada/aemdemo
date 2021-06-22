package com.exadel.kaliada.core.models;


import com.adobe.cq.wcm.core.components.models.Image;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@Model(adaptables = SlingHttpServletRequest.class,
       resourceType = ByLineModel.RESOURCE_TYPE,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ByLineModel {
    protected static final String RESOURCE_TYPE = "aemdemo/components/byline";
    private static final Logger LOGGER = LoggerFactory.getLogger(ByLineModel.class);

    @ValueMapValue
    private String name;

    @ValueMapValue
    private List<String> occupations;

    @Self
    private Image image;

    public String getName(){
        return name;
    }

    public List<String> getOccupations() {
        if (occupations != null){
            Collections.sort(occupations);
            return occupations;
        }else {
            return Collections.emptyList();
        }
    }

    public boolean isEmpty() {
        Image image = getImage();
        if (StringUtils.isBlank(name)){
            return true;
        }else if (occupations == null || occupations.isEmpty()){
            return true;
        }else if (image == null || StringUtils.isBlank(image.getSrc())){
            return true;
        }else {
            return false;
        }
    }

    private Image getImage(){
        return image;
    }

    @PostConstruct
    private void init(){
        LOGGER.info("init ByLineModel");
    }
}
