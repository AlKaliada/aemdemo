package com.exadel.kaliada.core.utils;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import java.util.HashMap;
import java.util.Map;

public class ResourceResolverUtil {

    private static final String SERVICE_USER = "aemdemoserviceuser";

    private ResourceResolverUtil() {}

    public static ResourceResolver getResourceResolver(ResourceResolverFactory resourceResolverFactory) throws LoginException {
        Map<String, Object> authInfo = new HashMap<String, Object>();
        authInfo.put(ResourceResolverFactory.SUBSERVICE, SERVICE_USER);
        return resourceResolverFactory.getServiceResourceResolver(authInfo);
    }
}
