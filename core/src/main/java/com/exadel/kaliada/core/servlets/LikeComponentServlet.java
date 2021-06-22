/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.exadel.kaliada.core.servlets;

import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.lang.module.ResolutionException;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */
@Component(service = { Servlet.class })
@SlingServletPaths(value = "/bin/like")
@ServiceDescription("Like component Servlet")
public class LikeComponentServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(LikeComponentServlet.class);
    private static final String NODE_NAME = "/content/aemdemo/us/en/harvard/jcr:content/root/container/like";
    private static final long START_COUNT = 0;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        super.doGet(request, response);
    }

    @Override
    protected void doPost(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
        try{
            Resource resource = req.getResource();
            ResourceResolver resourceResolver = resource.getResourceResolver();
            Session session = resourceResolver.adaptTo(Session.class);
            Node node = session.getNode(NODE_NAME);
            String propertyName = req.getParameterNames().nextElement();
            if (!node.hasProperty(propertyName)){
                node.setProperty(propertyName, START_COUNT);
            }
            long likes = node.getProperty(propertyName).getValue().getLong();
            likes = changeLikeValue(likes, req.getParameter(propertyName));
            node.getProperty(propertyName).setValue(likes);
            session.save();
            resp.setContentType("text/html");
            resp.getWriter().write(String.valueOf(likes));
        }catch (RepositoryException e){
            logger.error(e.getMessage());
        }
    }

    private long changeLikeValue(long value, String action){
        return  action.equals("decrement") ? --value : ++value;
    }
}
