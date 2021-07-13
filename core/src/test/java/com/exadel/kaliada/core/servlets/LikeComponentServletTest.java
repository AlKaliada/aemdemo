package com.exadel.kaliada.core.servlets;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
class LikeComponentServletTest {

    private AemContext aemContext = new AemContext();
    private LikeComponentServlet likeComponentServlet = new LikeComponentServlet();
    private MockSlingHttpServletRequest request;
    private MockSlingHttpServletResponse response;

    @BeforeEach
    private void setUp() {
        request = aemContext.request();
        response = aemContext.response();
        aemContext.load().json("/child1page.json", "/content/aemdemo/en/post-328695");
        aemContext.load().json("/child1page.json", "/content/aemdemo/ru/post-328695");
    }

    @Test
    public void doPostEnLocale() throws ServletException, IOException {

        request.addRequestParameter("url", "/content/aemdemo/en/post-328695.html");
        request.addRequestParameter("likeCounter", "increment");

        likeComponentServlet.doPost(request, response);

        assertEquals("3", response.getOutputAsString());
    }

    @Test
    public void doPostRuLocale() throws ServletException, IOException {

        request.addRequestParameter("url", "/content/aemdemo/ru/post-328695.html");
        request.addRequestParameter("dislikeCounter", "decrement");

        likeComponentServlet.doPost(request, response);

        assertEquals("0", response.getOutputAsString());
    }
}