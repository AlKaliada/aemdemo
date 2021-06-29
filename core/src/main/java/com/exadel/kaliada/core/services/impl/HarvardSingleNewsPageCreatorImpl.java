package com.exadel.kaliada.core.services.impl;

import com.adobe.granite.translation.api.TranslationConstants;
import com.adobe.granite.translation.api.TranslationException;
import com.adobe.granite.translation.api.TranslationManager;
import com.adobe.granite.translation.api.TranslationService;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.exadel.kaliada.core.services.HarvardSingleNewsPageCreator;
import com.exadel.kaliada.core.utils.ResourceResolverUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Map;
import java.util.stream.StreamSupport;

@Slf4j
@Component(service = HarvardSingleNewsPageCreator.class)
public class HarvardSingleNewsPageCreatorImpl implements HarvardSingleNewsPageCreator {

    private static final String PAGE_PATH = "/content/aemdemo/en";
    private static final String PAGE_TEMPLATE = "/conf/aemdemo/settings/wcm/templates/harvard-single-news-page";
    private static final String PAGE_TITLE_NODE = "/jcr:content/root/container/title?jcr:title";
    private static final String PAGE_COPY_PATH = "/content/aemdemo/ru/";
    private static final String SOURCE_LANGUAGE = "en";
    private static final String TARGET_LANGUAGE = "ru";
    private static final String TAG_NAMESPACE_PATH = "/content/cq:tags/harvard-news";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private TranslationManager translationManager;

    @Override
    public void createPage(String pageName, Map<String, String> pagePropertyToValue) {
        log.info("start creating page {}", pageName);
        try (ResourceResolver resourceResolver = ResourceResolverUtil.getResourceResolver(resourceResolverFactory)) {
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Page page = pageManager.create(PAGE_PATH, pageName, PAGE_TEMPLATE, pagePropertyToValue.get(PAGE_TITLE_NODE), true);
            Page pageCopy = copyPage(pageManager, page);
            setComponentsValues(resourceResolver, page, pagePropertyToValue);
            translateValues(page, pagePropertyToValue);
            setComponentsValues(resourceResolver, pageCopy, pagePropertyToValue);
        } catch (LoginException | WCMException | PersistenceException e) {
            log.error("All broken",e);
        } catch (TranslationException e) {
            log.error("Cannot translate", e);
        }
    }

    /**
     * set values of the created page components
     * @param resourceResolver
     * @param page - created page
     * @param pagePropertyToValue - pairs components of news page (tag, image, title, text) and their values
     * @throws PersistenceException
     */

    private void setComponentsValues(ResourceResolver resourceResolver, Page page, Map<String, String> pagePropertyToValue) throws PersistenceException {
        String pagePath = page.getPath();
        for (Map.Entry<String, String> entry : pagePropertyToValue.entrySet()) {
            String[] strArray = entry.getKey().split("\\?");
            if (strArray[0].equals("tag")) {
                setTag(resourceResolver, page, entry.getValue());
            } else {
                Resource resource = resourceResolver.getResource(String.join("", pagePath, strArray[0]));
                ModifiableValueMap valueMap = resource.adaptTo(ModifiableValueMap.class);
                valueMap.put(strArray[1], entry.getValue());
                resourceResolver.commit();
            }
        }
    }

    /**
     * create tag by tag name or return it if already exist and set it to created page
     * @param resourceResolver
     * @param page - created page
     * @param tagString - tag name
     */

    private void setTag(ResourceResolver resourceResolver, Page page, String tagString) {
        try {
            TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
            Resource resource = page.getContentResource();
            Tag tag = tagManager.createTag(String.join("/", TAG_NAMESPACE_PATH, tagString), null, null, true);
            Tag[] tagArray = {tag};
            tagManager.setTags(resource, tagArray, true);
        } catch (InvalidTagFormatException e) {
            log.error("cannot create tag {}", tagString, e);
        }
    }

    private Page copyPage(PageManager pageManager, Page page) throws WCMException {
        String destination = String.join("", PAGE_COPY_PATH, page.getName());
        return pageManager.copy(page, destination, null, true, false, true);
    }

    /**
     * translate page components values
     * @param page - page to translate
     * @param pagePropertyToValue - pairs components of news page (tag, image, title, text) and their values that will be translate
     * @throws TranslationException
     */

    private void translateValues(Page page, Map<String, String> pagePropertyToValue) throws TranslationException {
        TranslationService translationService = translationManager.createTranslationService(page.getContentResource());
        for (Map.Entry<String, String> entry : pagePropertyToValue.entrySet()) {
            if (!entry.getKey().equals("tag") && !entry.getKey().equals(HarvardNewsParserImpl.IMAGE_PATH) && !entry.getKey().equals(HarvardNewsParserImpl.TEXT_PATH_IS_REACH_PROPERTY)) {
                String translatedString = translationService
                        .translateString(entry.getValue(), SOURCE_LANGUAGE, TARGET_LANGUAGE, TranslationConstants.ContentType.HTML, null)
                        .getTranslation();
                entry.setValue(translatedString);
            }
        }
    }

    @Override
    public void deleteNewsDuplicates(Map<String, String> newsIdToLink) {
        try (ResourceResolver resourceResolver = ResourceResolverUtil.getResourceResolver(resourceResolverFactory)) {
            Resource resource = resourceResolver.getResource(PAGE_PATH);
            StreamSupport.stream(resource.getChildren().spliterator(), false)
                    .map(Resource::getName)
                    .forEach(newsIdToLink::remove);
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }
}
