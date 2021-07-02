package com.exadel.kaliada.core.services.impl;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.exadel.kaliada.core.models.NewsModel;
import com.exadel.kaliada.core.services.HarvardNewsSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.jsoup.Jsoup;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
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
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public List<NewsModel> getAllNews(SlingHttpServletRequest request, int offset, int limit, String tagName, String locale) {
        try {
            List<NewsModel> news = new ArrayList<>();
            Query query = getQuery(request, tagName, locale, offset, limit);
            QueryResult queryResult = query.execute();
            NodeIterator nodeIterator = queryResult.getNodes();
            while (nodeIterator.hasNext()) {
                Node node = nodeIterator.nextNode();
                NewsModel newsModel = new NewsModel();
                newsModel.setReference(node.getParent().getPath() + EXTENSION);
                Node titleNode = node.getNode(TITLE_NODE);
                newsModel.setTitle(titleNode.getProperty(TITLE_PROPERTY).getValue().getString());
                newsModel.setTags(Arrays.stream(node.getProperty(TAG_PROPERTY).getValues())
                        .map(Object::toString)
                        .map(tag->tag.replace(TAG_NAME_SPACE, ""))
                        .collect(Collectors.toList()));
                Node imageNode = node.getNode(IMAGE_NODE);
                newsModel.setImage(imageNode.getProperty(IMAGE_PROPERTY).getValue().getString());
                Node textNode = node.getNode(TEXT_NODE);
                newsModel.setText(getSummaryArticle(COUNT_WORDS_SUMMARY_ARTICLE, Jsoup.parse(textNode.getProperty(TEXT_PROPERTY).getValue().getString()).text()));
                Node likeNode = node.getNode(LIKE_NODE);
                long likes = likeNode.hasProperty(LIKE_PROPERTY) ? likeNode.getProperty(LIKE_PROPERTY).getValue().getLong() : 0;
                long dislikes = likeNode.hasProperty(DISLIKE_PROPERTY) ? likeNode.getProperty(DISLIKE_PROPERTY).getValue().getLong() : 0;
                newsModel.setLikes(likes);
                newsModel.setDislikes(dislikes);
                news.add(newsModel);
            }
            return news;
        } catch (RepositoryException e) {
            log.error("cannot get news", e);
        }
        return Collections.emptyList();
    }

    private Query getQuery(SlingHttpServletRequest request, String tagName, String locale, int offset, int limit) throws RepositoryException {
        ResourceResolver resourceResolver = request.getResourceResolver();
        Session session = resourceResolver.adaptTo(Session.class);
        QueryManager queryManager = session.getWorkspace().getQueryManager();
        String newQuery = tagName == null || tagName.length() == 0
                ? BASE_QUERY.replace(LOCALE_NAME_IN_BASE_QUERY, locale)
                : BASE_QUERY.replace("%", tagName).replace(LOCALE_NAME_IN_BASE_QUERY, locale);
        Query query = queryManager.createQuery(newQuery, Query.JCR_SQL2);
        query.setOffset(offset);
        query.setLimit(limit);
        return query;
    }

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
        Page rootPage = pageManager.getPage(ROOT_PAGE);
        String domainName = request.getRequestPathInfo().getResourcePath().substring(0, request.getRequestPathInfo().getResourcePath().indexOf("/"));
        Map<String, String> tagToLink = new TreeMap<>();
        rootPage.listChildren().forEachRemaining(page -> tagToLink.putAll(Arrays.stream(page.getTags())
                .map(Tag::getName)
                .collect(Collectors.toMap(Function.identity(), tag->String.join("", domainName, URI_HARVARD_NEWS, TAG_URL_PARAMETER, tag.replace("&", "%26").replace("+","%2b"))))));
        return tagToLink;
    }
}
