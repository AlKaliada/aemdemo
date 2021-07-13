package com.exadel.kaliada.core.services.impl;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.exadel.kaliada.core.models.NewsModel;
import com.exadel.kaliada.core.services.HarvardNewsSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.factory.ModelFactory;
import org.jsoup.Jsoup;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component(service = HarvardNewsSearchService.class, immediate = true)
public class HarvardNewsSearchServiceImpl implements HarvardNewsSearchService {
    private static final String BASE_QUERY = "SELECT * FROM [cq:PageContent] AS s WHERE ISDESCENDANTNODE([/content/aemdemo/locale]) AND s.[cq:template]='/conf/aemdemo/settings/wcm/templates/harvard-single-news-page' AND s.[cq:tags] LIKE 'harvard-news:%' order by s.[jcr:created] desc";
    private static final int COUNT_WORDS_SUMMARY_ARTICLE = 200;
    private static final String LOCALE_NAME_IN_BASE_QUERY = "locale";
    private static final String TITLE_NODE = "root/container/title";
    private static final String TITLE_PROPERTY = "jcr:title";
    private static final String TAG_PROPERTY = "cq:tags";
    private static final String TAG_NAME_SPACE = "harvard-news:";
    private static final String IMAGE_NODE = "root/container/image";
    private static final String IMAGE_PROPERTY = "fileReference";
    private static final String TEXT_NODE = "root/container/text";
    private static final String TEXT_PROPERTY = "text";
    private static final String LIKE_NODE = "root/container/like";
    private static final String LIKE_PROPERTY = "likeCounter";
    private static final String DISLIKE_PROPERTY = "dislikeCounter";
    private static final String ROOT_PAGE = "/content/aemdemo/en";
    private static final String EXTENSION = ".html";
    private static final String TAG_URL_PARAMETER = "?tag=";
    private static final String URI_HARVARD_NEWS = "/content/aemdemo/en/harvard-news.html";

    @Reference
    private ModelFactory modelFactory;

    @Override
    public List<NewsModel> getAllNews(SlingHttpServletRequest request, int offset, int limit, String tagName, String locale) {
        List<NewsModel> news = new ArrayList<>();
        try {
            ResourceResolver resourceResolver = request.getResourceResolver();
            Query query = getQuery(request, tagName, locale, offset, limit);
            QueryResult queryResult = query.execute();
            NodeIterator nodeIterator = queryResult.getNodes();
            while (nodeIterator.hasNext()) {
                Node node = nodeIterator.nextNode();
                Resource resource = resourceResolver.getResource(node.getPath());
                if (resource == null) {
                    continue;
                }
                NewsModel newsModel = modelFactory.createModel(resource, NewsModel.class);
                newsModel.setReference(Optional.ofNullable(resource.getParent())
                        .map(res -> res.getPath() + EXTENSION)
                        .orElse(null));
                newsModel.setTitle(extractPropertyFromChildResource(resource, TITLE_NODE, TITLE_PROPERTY, String.class));
                newsModel.setTags(Arrays.stream(resource.getValueMap().get(TAG_PROPERTY, new String[0]))
                        .map(tag->tag.replace(TAG_NAME_SPACE, StringUtils.EMPTY))
                        .collect(Collectors.toList()));
                newsModel.setImage(extractPropertyFromChildResource(resource, IMAGE_NODE, IMAGE_PROPERTY, String.class));
                newsModel.setText(getSummaryArticle(COUNT_WORDS_SUMMARY_ARTICLE, Jsoup.parse(extractPropertyFromChildResource(resource, TEXT_NODE, TEXT_PROPERTY, String.class)).text()));
                newsModel.setLikes(extractPropertyFromChildResource(resource, LIKE_NODE, LIKE_PROPERTY, Long.class));
                newsModel.setDislikes(extractPropertyFromChildResource(resource, LIKE_NODE, DISLIKE_PROPERTY, Long.class));
                news.add(newsModel);
            }
            return news;
        } catch (RepositoryException e) {
            log.error("cannot get news", e);
        }
        return news;
    }

    private <T> T extractPropertyFromChildResource(Resource resource, String childName, String propertyName, Class<T> returnType) {
        return Optional.ofNullable(resource.getChild(childName))
                .map(Resource::getValueMap)
                .map(valueMap -> valueMap.get(propertyName, returnType))
                .orElse(null);
    }

    /**
     *
     * @param request SlingHttpServletRequest
     * @param tagName tag name to search news by tag
     * @param locale locale name to search news by locale
     * @param offset how many news need to skip
     * @param limit how many news need to show
     * @return return query based on incoming parameters
     * @throws RepositoryException
     */
    private Query getQuery(SlingHttpServletRequest request, String tagName, String locale, int offset, int limit) throws RepositoryException {
        ResourceResolver resourceResolver = request.getResourceResolver();
        Session session = resourceResolver.adaptTo(Session.class);
        if (session == null) {
            throw new NoSuchElementException("cannot get session");
        }
        QueryManager queryManager = session.getWorkspace().getQueryManager();
        String newQuery = tagName == null || tagName.length() == 0
                ? BASE_QUERY.replace(LOCALE_NAME_IN_BASE_QUERY, locale)
                : BASE_QUERY.replace("%", tagName).replace(LOCALE_NAME_IN_BASE_QUERY, locale);
        Query query = queryManager.createQuery(newQuery, Query.JCR_SQL2);
        query.setOffset(offset);
        query.setLimit(limit);
        return query;
    }

    /**
     *
     * @param countWords count words in summary article
     * @param article whole article
     * @return summary article
     */
    private String getSummaryArticle(int countWords, String article) {
        int index = 0;
        while (countWords != 0) {
            if (article.indexOf(" ", ++index) == -1) {
                break;
            }
            index = article.indexOf(" ", index);
            countWords--;
        }
        return article.substring(0, index);
    }

    @Override
    public Map<String, String> getAllTags(SlingHttpServletRequest request) {
        ResourceResolver resourceResolver = request.getResourceResolver();
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        Page rootPage = Optional.ofNullable(pageManager)
                .map(pManager -> pManager.getPage(ROOT_PAGE))
                .orElse(null);
        if (rootPage == null) {
            return Collections.emptyMap();
        }
        String domainName = request.getRequestPathInfo().getResourcePath().substring(0, request.getRequestPathInfo().getResourcePath().indexOf("/"));
        Map<String, String> tagToLink = new TreeMap<>();
        rootPage.listChildren().forEachRemaining(page -> tagToLink.putAll(Arrays.stream(page.getTags())
                .map(Tag::getName)
                .collect(Collectors.toMap(Function.identity(), tag->String.join("", domainName, URI_HARVARD_NEWS, TAG_URL_PARAMETER, tag.replace("&", "%26").replace("+","%2b"))))));
        return tagToLink;
    }
}
