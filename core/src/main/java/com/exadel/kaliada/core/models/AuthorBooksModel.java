package com.exadel.kaliada.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.inject.Inject;
import java.util.*;

@Model(adaptables = SlingHttpServletRequest.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AuthorBooksModel {

    @Inject
    private Resource componentResource;

    @ValueMapValue
    private String authorName;

    @ValueMapValue
    private List<String> books;

    public String getAuthorName(){
        return authorName;
    }

    public List<String> getAuthorBooks(){
        return books == null ? Collections.emptyList() : books;
    }

    public List<Map<String, String>> getBookDetailsWithMap(){
        List<Map<String, String>> bookDetails = new ArrayList<>();
        Resource bookDetail = componentResource.getChild("bookdetailswithmap");
        if (bookDetail != null){
            for (Resource book : bookDetail.getChildren()) {
                Map<String,String> bookMap = new HashMap<>();
                bookMap.put("bookName",book.getValueMap().get("bookname",String.class));
                bookMap.put("bookSubject",book.getValueMap().get("booksubject",String.class));
                bookMap.put("publishYear",book.getValueMap().get("publishymiear",String.class));
                bookDetails.add(bookMap);
            }
        }
        return bookDetails;
    }
}
