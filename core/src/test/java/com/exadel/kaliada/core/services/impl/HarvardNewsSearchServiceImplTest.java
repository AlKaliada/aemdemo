package com.exadel.kaliada.core.services.impl;

import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.exadel.kaliada.core.models.NewsModel;
import io.wcm.testing.mock.aem.MockTagManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
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
        aemContext.load().json("/rootpage.json", "/content/aemdemo/en");
        Resource child1Resource = aemContext.load().json("/child1page.json", "/content/aemdemo/en/child1");
        Resource child2Resource = aemContext.load().json("/child2page.json", "/content/aemdemo/en/child2");

        MockTagManager tagManager = (MockTagManager) aemContext.resourceResolver().adaptTo(TagManager.class);

        Tag[] tag1 = {aemContext.create().tag("harvard-news:tag1")};
        Tag[] tag2 = {aemContext.create().tag("harvard-news:tag2")};

        if (tagManager != null) {
            tagManager.setTags(child1Resource.getChild("jcr:content"), tag1);
            tagManager.setTags(child2Resource.getChild("jcr:content"), tag2);
        }
    }

    @Test
    void getAllNews() {
        List<NewsModel> allNews = harvardNewsSearchService.getAllNews(request, 0, 2, "tag1", "en");

        assertAll(
                () -> assertEquals(1, allNews.size(), "news size"),
                () -> assertEquals("text text ", allNews.get(0).getText(), "text property"),
                () -> assertEquals("Child1", allNews.get(0).getTitle(), "title property"),
                () -> assertEquals("child1 image", allNews.get(0).getImage(), "image reference"),
                () -> assertEquals(2, allNews.get(0).getLikes(), "likes"),
                () -> assertEquals(1, allNews.get(0).getDislikes(), "dislikes"),
                () -> assertEquals(List.of("tag1"), allNews.get(0).getTags(), "tags")
        );
    }

    @Test
    void getAllNewsWithoutTag() {
        List<NewsModel> allNews = harvardNewsSearchService.getAllNews(request, 0, 2, null, "en");

        assertAll(
                () -> assertEquals(2, allNews.size(), "news size"),
                () -> assertEquals("text text ", allNews.get(1).getText(), "text property"),
                () -> assertEquals("word word ", allNews.get(0).getText(), "text property"),
                () -> assertEquals("Child1", allNews.get(1).getTitle(), "title property"),
                () -> assertEquals("Child2", allNews.get(0).getTitle(), "title property"),
                () -> assertEquals("child1 image", allNews.get(1).getImage(), "image reference"),
                () -> assertEquals("child2 image", allNews.get(0).getImage(), "image reference"),
                () -> assertEquals(2, allNews.get(1).getLikes(), "likes"),
                () -> assertEquals(3, allNews.get(0).getLikes(), "likes"),
                () -> assertEquals(1, allNews.get(1).getDislikes(), "dislikes"),
                () -> assertEquals(4, allNews.get(0).getDislikes(), "dislikes"),
                () -> assertEquals(List.of("tag1"), allNews.get(1).getTags(), "tags"),
                () -> assertEquals(List.of("tag2"), allNews.get(0).getTags(), "tags")
        );
    }

    @Test
    void getAllTags() throws InvalidTagFormatException {
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setResourcePath("localhost:4502/content/aemdemo/en/harvard-news.html");

        Map<String, String> allTags = harvardNewsSearchService.getAllTags(request);

        assertAll(
                () -> assertEquals(2, allTags.size()),
                () -> assertTrue(allTags.containsKey("tag1"), "contains tag1"),
                () -> assertTrue(allTags.containsKey("tag2"), "contains tag2"),
                () -> assertEquals("localhost:4502/content/aemdemo/en/harvard-news.html?tag=tag1", allTags.get("tag1")),
                () -> assertEquals("localhost:4502/content/aemdemo/en/harvard-news.html?tag=tag2", allTags.get("tag2"))
        );
    }
}