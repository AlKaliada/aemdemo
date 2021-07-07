package com.exadel.kaliada.core.servlets;

import com.exadel.kaliada.core.services.HarvardNewsParser;
import com.exadel.kaliada.core.services.HarvardSingleNewsPageCreator;
import com.exadel.kaliada.core.utils.HarvardNewsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Slf4j
@Component(service = Servlet.class)
@SlingServletResourceTypes(resourceTypes = "aemdemo/harvard-console",
methods = {HttpConstants.METHOD_POST})
public class UpdateNewsServlet extends SlingAllMethodsServlet {

    @Reference
    private HarvardSingleNewsPageCreator harvardSingleNewsPageCreator;

    @Reference
    private HarvardNewsParser harvardNewsParser;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        HarvardNewsUtil.updateNews(harvardNewsParser, harvardSingleNewsPageCreator);
    }
}
