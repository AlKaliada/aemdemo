package com.exadel.kaliada.core.services.impl;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.exadel.kaliada.core.services.HarvardNewsParser;
import com.exadel.kaliada.core.utils.ResourceResolverUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component(service = HarvardNewsParser.class)
public class HarvardNewsParserImpl implements HarvardNewsParser {

    private static final String TITLE_PATH = "/jcr:content/root/container/title?jcr:title";
    private static final String TEXT_PATH = "/jcr:content/root/container/text?text";
    private static final String ASSET_PATH = "/content/dam/aemdemo";
    private static final String DEFAULT_IMAGE_PATH = "/content/dam/aemdemo/harvard-university-free-online-courses.jpg";
    private static final String USER_AGENT = "Chrome/4.0.249.0 Safari/532.5";
    private static final String REFERRER = "https://www.google.com";
    public static final String TEXT_PATH_IS_REACH_PROPERTY = "/jcr:content/root/container/text?textIsRich";
    public static final String IMAGE_PATH = "/jcr:content/root/container/image?fileReference";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public Map<String, String> getAllNews() {
        try {
            log.info("Start loading news");
            Map<String, String> newsIdToLink = new HashMap<>();
            Document document = Jsoup.connect("https://news.harvard.edu/gazette/")
                    .userAgent(USER_AGENT)
                    .referrer(REFERRER)
                    .get();
            Elements news = document.select("article[id]");
            for (Element element : news) {
                String id = element.attr("id");
                Element linkElement = element.selectFirst("a[href]");
                String link = linkElement != null ? linkElement.attr("href") : null;
                if (link != null){
                    newsIdToLink.put(id, link);
                }
            }
            return newsIdToLink;
        } catch (IOException e) {
            log.error("cannot parse news", e);
        }
        return Collections.emptyMap();
    }

    @Override
    public Map<String, String> parseSingleNews(String link) {
        try {
            Document document = Jsoup.connect(link)
                    .userAgent(USER_AGENT)
                    .referrer(REFERRER)
                    .get();

            String tag = document.selectFirst("a.article-titles__cat-link").text();
            String title = document.selectFirst("h1.article-titles__title").text();
            Element imageElement = document.select("div.article-media__media-content")
                    .select("img[src]").first();
            String image = imageElement != null ? imageElement.attr("src") : null;
            String text = document.select("div.article-body").html();
            Map<String, String> resourceToValue = new HashMap<>();
            resourceToValue.put("tag", tag);
            resourceToValue.put(IMAGE_PATH, uploadImageToAssets(image));
            resourceToValue.put(TITLE_PATH, title);
            resourceToValue.put(TEXT_PATH, text);
            resourceToValue.put(TEXT_PATH_IS_REACH_PROPERTY, "true");
            return resourceToValue;
        } catch (IOException e) {
            log.error("cannot parse news {}", link, e);
        }
        return Collections.emptyMap();
    }

    /**
     * upload picture to Assets by link
     * @param link - link of picture
     * @return asset reference
     */

    private String uploadImageToAssets(String link) {
        try (ResourceResolver resourceResolver = ResourceResolverUtil.getResourceResolver(resourceResolverFactory)) {
            AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);
            URL url = new URL(link);
            String fileName = String.join("/", ASSET_PATH, url.getFile().replaceFirst("/.+/", ""));
            String mimeType = url.openConnection().getContentType();
            InputStream inputStream = new BufferedInputStream(url.openStream());
            Asset asset = assetManager.createAsset(fileName, inputStream, mimeType, true);
            return asset.getPath();
        } catch (IOException e) {
            log.error("cannot upload image from {}", link, e);
        } catch (LoginException e) {
            log.error("cannot get resource resolver", e);
        }
        return DEFAULT_IMAGE_PATH;
    }
}
