package ru.grabber.service;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import ru.grabber.model.Post;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {
    private static final Logger log = Logger.getLogger(HabrCareerParse.class);
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PREFIX = "/vacancies?page=";
    private static final String SUFFIX = "&q=Java%20developer&type=all";

    @Override
    public List<Post> fetch() {
        var result = new ArrayList<Post>();
        try {
            int pageNumber = 1;
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
                LocalDateTime createdDate = OffsetDateTime.parse(datetimeAttr).toLocalDateTime();
                var post = new Post();
                post.setName(vacancyName);
                post.setLink(link);
                post.setCreated(createdDate);
                result.add(post);
            });
        } catch (IOException e) {
            log.error("When load page", e);
        }
        return result;
    }
}
