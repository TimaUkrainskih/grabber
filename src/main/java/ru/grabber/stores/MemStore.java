package ru.grabber.stores;

import ru.grabber.model.Post;
import ru.grabber.service.Store;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemStore implements Store {

    private final List<Post> posts = new ArrayList<>();

    @Override
    public void save(Post post) {
        posts.add(post);
    }

    @Override
    public List<Post> getAll() {
        return posts;
    }

    @Override
    public Optional<Post> findById(Long id) {
        return posts.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
    }
}