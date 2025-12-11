package ru.grabber.service;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.grabber.model.Post;
import ru.grabber.utils.DateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {
    private static final Logger log = Logger.getLogger(HabrCareerParse.class);
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PREFIX = "/vacancies?page=";
    private static final String SUFFIX = "&q=Java%20developer&type=all";
    private static final int COUNT_PAGE_OF_PARSING = 5;
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> fetch() {
        var result = new ArrayList<Post>();
        try {
            for (int pageNumber = 1; pageNumber <= COUNT_PAGE_OF_PARSING; pageNumber++) {
                String fullLink = "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, pageNumber, SUFFIX);
                var connection = Jsoup.connect(fullLink);
                var document = connection.get();
                var rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> {
                    var titleElement = row.select(".vacancy-card__title").first();
                    var linkElement = titleElement.child(0);
                    String vacancyName = titleElement.text();
                    String link = String.format("%s%s", SOURCE_LINK,
                            linkElement.attr("href"));
                    Element createdDateElement = row.select(".vacancy-card__date time").first();
                    String datetimeAttr = createdDateElement.attr("datetime");
                    LocalDateTime createdDate = dateTimeParser.parse(datetimeAttr);
                    String description = fetchDescription(link);
                    var post = new Post();
                    post.setName(vacancyName);
                    post.setLink(link);
                    post.setCreated(createdDate);
                    post.setDescription(description);
                    result.add(post);
                });
            }
        } catch (IOException e) {
            log.error("When load page", e);
        }
        return result;
    }

    private String fetchDescription(String link) {
        String description = "";
        try {
            Connection connection = Jsoup.connect(link);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-description__text .style-ugc");
            description = rows.text();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return description;
    }
}
