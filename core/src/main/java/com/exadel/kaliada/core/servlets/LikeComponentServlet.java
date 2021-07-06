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

import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servlet to increment/decrement likes/dislikes values in Like component
 * @author akaliada
 */

@Slf4j
@Component(service = Servlet.class)
@SlingServletPaths(value = "/bin/like")
public class LikeComponentServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final String LIKE_COMPONENT_PATH = "/jcr:content/root/container/like";
    private static final String URL_PARAMETER = "url";
    private String resourceName;
    private String propertyName;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        super.doGet(request, response);
    }

    /**
     * increment/decrement likes/dislikes values in Like component in two locales (ru and en)
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */

    @Override
    protected void doPost(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
        ResourceResolver resourceResolver = req.getResourceResolver();
        req.getParameterNames().asIterator().forEachRemaining(parameter->{
            if (parameter.equals(URL_PARAMETER)) {
                resourceName = getResourceName(req.getParameter(URL_PARAMETER));
            } else {
                propertyName = parameter;
            }
        });
        Resource resource = resourceResolver.getResource(resourceName);
        long likes = changeLikes(resource, req);
        String secondPageResource = resourceName.contains("/en/")
                ? resourceName.replace("/en/", "/ru/")
                : resourceName.replace("/ru/", "/en/");
        resource = resourceResolver.getResource(secondPageResource);
        changeLikes(resource, req);
        resourceResolver.commit();
        log.info("++++++++++++++++++++++++++++++++++++++++++");
        log.info(String.valueOf(likes));
        log.info("++++++++++++++++++++++++++++++++++++++++++");
        resp.setContentType("text/html");
        resp.getWriter().write(String.valueOf(likes));
    }

    /**
     * get likes/dislikes value if exists or set 0, and than set new value
     * @param resource
     * @param req
     * @return - new value of like/dislike
     */

    private long changeLikes(Resource resource, SlingHttpServletRequest req) {
        ModifiableValueMap valueMap = resource.adaptTo(ModifiableValueMap.class);
        long likes = valueMap.get(propertyName, 0);
        likes = changeLikeValue(likes, req.getParameter(propertyName));
        valueMap.put(propertyName, likes);
        return likes;
    }

    /**
     * get resource (page) path in repo from url of the page from which request came
     * @param url - url of the page from which request came
     * @return - resource (page) path in repo
     */

    private String getResourceName(String url) {
        Pattern pattern = Pattern.compile("(\\/content.+)\\.html");
        Matcher matcher = pattern.matcher(url);
        matcher.find();
        return String.join("", matcher.group(1), LIKE_COMPONENT_PATH);
    }

    /**
     * increment/decrement likes/dislikes values
     * @param value - current like/dislike value
     * @param action - increment/decrement
     * @return - new value of like/dislike
     */

    private long changeLikeValue(long value, String action) {
        return  action.equals("decrement") ? --value : ++value;
    }
}
