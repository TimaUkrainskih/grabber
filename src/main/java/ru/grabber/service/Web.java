package ru.grabber.service;

import io.javalin.Javalin;
import ru.grabber.service.Store;

public class Web {
    private final Store store;

    public Web(Store store) {
        this.store = store;
    }

    public void start(int port) {
        Javalin app = Javalin.create(config -> {
            config.http.defaultContentType = "text/html; charset=utf-8";
        });
        app.start(port);
        StringBuilder page = new StringBuilder();
        store.getAll().forEach(post -> page.append(post.toString()).append(System.lineSeparator()));
        app.get("/", ctx -> {
            ctx.contentType("text/html; charset=utf-8");
            ctx.result(page.toString());
        });
    }
}
