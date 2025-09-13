package ru;

import org.apache.log4j.Logger;
import ru.grabber.model.Post;
import ru.grabber.service.Config;
import ru.grabber.service.SchedulerManager;
import ru.grabber.service.SuperJobGrab;
import ru.grabber.stores.JdbcStore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;

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
            var post = new Post();
            post.setName("Super Java Job");
            post.setLink("https://example.com/posts/1");
            post.setDescription("Вакансия для Java разработчика");
            post.setCreated(LocalDateTime.now());
            store.save(post);
            scheduler.init();
            scheduler.load(
                    Integer.parseInt(config.get("rabbit.interval")),
                    SuperJobGrab.class,
                    store
            );
            Thread.sleep(10000);
        } catch (SQLException e) {
            log.error("When create a connection", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
