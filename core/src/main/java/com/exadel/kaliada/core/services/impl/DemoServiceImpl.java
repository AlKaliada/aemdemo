package com.exadel.kaliada.core.services.impl;

import com.day.cq.wcm.api.Page;
import com.exadel.kaliada.core.services.DemoService;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.osgi.service.component.annotations.Component;

import java.util.Iterator;

@Component(service = DemoService.class)
public class DemoServiceImpl implements DemoService {

    @OSGiService
    private DemoService demoService;

    @Override
    public Iterator<Page> getPages() {
        return null;
    }
}
