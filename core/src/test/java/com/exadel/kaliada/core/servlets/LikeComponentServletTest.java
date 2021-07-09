package com.exadel.kaliada.core.servlets;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
class LikeComponentServletTest {

    private AemContext aemContext = new AemContext();
    private LikeComponentServlet likeComponentServlet = new LikeComponentServlet();

    @Test
    void doPost() throws ServletException, IOException {
        aemContext.build().resource("/content/aemdemo/en/post-328695/jcr:content/root/container/like");
        aemContext.build().resource("/content/aemdemo/ru/post-328695/jcr:content/root/container/like");

        MockSlingHttpServletRequest request = aemContext.request();
        MockSlingHttpServletResponse response = aemContext.response();
        request.addRequestParameter("url", "/content/aemdemo/en/post-328695.html");
        request.addRequestParameter("likeCounter", "increment");

        likeComponentServlet.doPost(request, response);

        assertEquals("/content/aemdemo/en/post-328695.html", request.getParameter("url"));
        assertEquals("increment", request.getParameter("likeCounter"));
        assertEquals("1", response.getOutputAsString());
    }
}