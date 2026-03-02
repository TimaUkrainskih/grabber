package ru;

import org.apache.log4j.Logger;
import ru.grabber.model.Post;
import ru.grabber.service.*;
import ru.grabber.stores.JdbcStore;
import ru.grabber.utils.impl.HabrCareerDateTimeParser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

    private static final Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        var config = new Config();
        config.load("application.properties");
        try (Connection connection = DriverManager.getConnection(
                config.get("db.url"),
                config.get("db.username"),
                config.get("db.password"));
             var scheduler = new SchedulerManager()) {
            var store = new JdbcStore(connection);
            HabrCareerParse habrCareerParse = new HabrCareerParse(
                    new HabrCareerDateTimeParser()
            );
            for (Post post : habrCareerParse.fetch()) {
                store.save(post);
            }
            scheduler.init();
            scheduler.load(
                    Integer.parseInt(config.get("rabbit.interval")),
                    SuperJobGrab.class,
                    store
            );
            new Web(store).start(Integer.parseInt(config.get("server.port")));
        } catch (SQLException e) {
            log.error("When create a connection", e);
        }
    }
}
