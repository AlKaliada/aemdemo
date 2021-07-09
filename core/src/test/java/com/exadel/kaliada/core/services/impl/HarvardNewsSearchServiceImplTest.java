package com.exadel.kaliada.core.services.impl;

import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.exadel.kaliada.core.models.NewsModel;
import io.wcm.testing.mock.aem.MockTagManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.oak.OakMockSlingRepository;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class HarvardNewsSearchServiceImplTest {

    private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_OAK);
    private MockSlingHttpServletRequest request;
    private HarvardNewsSearchServiceImpl harvardNewsSearchService;

    @BeforeEach
    private void init() {
        aemContext.registerInjectActivateService(new OakMockSlingRepository());
        harvardNewsSearchService = aemContext.registerInjectActivateService(new HarvardNewsSearchServiceImpl());

        request = aemContext.request();

        Page page = aemContext.create().page("/content/aemdemo/en");

        Page pageChild1 = aemContext.create().page("/content/aemdemo/en/child1", "/conf/aemdemo/settings/wcm/templates/harvard-single-news-page");

        Page pageChild2 = aemContext.create().page("/content/aemdemo/en/child2", "/conf/aemdemo/settings/wcm/templates/harvard-single-news-page");
        MockTagManager tagManager = (MockTagManager) aemContext.resourceResolver().adaptTo(TagManager.class);
        Tag[] tag1 = {aemContext.create().tag("harvard-news:tag1")};
        Tag[] tag2 = {aemContext.create().tag("harvard-news:tag2")};
        tagManager.setTags(pageChild1.getContentResource(), tag1);
        tagManager.setTags(pageChild2.getContentResource(), tag2);

    }

    @Test
    void getAllNews() {

        List<NewsModel> allNews = harvardNewsSearchService.getAllNews(request, 0, 2, "tag1", "en");
    }

    @Test
    void getAllTags() throws InvalidTagFormatException {
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setResourcePath("localhost:4502/content/aemdemo/en/harvard-news.html");

        Map<String, String> allTags = harvardNewsSearchService.getAllTags(request);

        assertAll(
                () -> assertEquals(2, allTags.size()),
                () -> assertTrue(allTags.containsKey("tag1")),
                () -> assertTrue(allTags.containsKey("tag2")),
                () -> assertEquals("localhost:4502/content/aemdemo/en/harvard-news.html?tag=tag1", allTags.get("tag1")),
                () -> assertEquals("localhost:4502/content/aemdemo/en/harvard-news.html?tag=tag2", allTags.get("tag2"))
        );
    }
}