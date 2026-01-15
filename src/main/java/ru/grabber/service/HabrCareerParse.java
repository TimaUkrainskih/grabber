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
        ArrayList<Post> result = new ArrayList<>();
        for (int page = 1; page <= COUNT_PAGE_OF_PARSING; page++) {
            result.addAll(fetchPage(page));
        }
        return result;
    }

    private List<Post> fetchPage(int pageNumber) {
        String url = buildPageLink(pageNumber);
        try {
            Document document = Jsoup.connect(url).get();
            return parseVacancyCards(document);
        } catch (IOException e) {
            log.error("When load page: " + url, e);
            return List.of();
        }
    }

    private String buildPageLink(int pageNumber) {
        return "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, pageNumber, SUFFIX);
    }

    private List<Post> parseVacancyCards(Document document) {
        ArrayList<Post> result = new ArrayList<>();
        Elements rows = document.select(".vacancy-card__inner");
        rows.forEach(row -> result.add(parseVacancy(row)));
        return result;
    }

    private Post parseVacancy(Element row) {
        Element titleElement = row.select(".vacancy-card__title").first();
        Element linkElement = titleElement.child(0);
        String name = titleElement.text();
        String link = SOURCE_LINK + linkElement.attr("href");
        Element dateElement = row.select(".vacancy-card__date time").first();
        LocalDateTime created = dateTimeParser.parse(dateElement.attr("datetime"));
        String description = fetchDescription(link);
        Post post = new Post();
        post.setName(name);
        post.setLink(link);
        post.setCreated(created);
        post.setDescription(description);
        return post;
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
