package ru.grabber.stores;

import ru.grabber.model.Post;
import ru.grabber.service.Store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MemStore implements Store {

    private final Map<Long, Post> posts = new HashMap<>();

    @Override
    public void save(Post post) {
        posts.put(post.getId(), post);
    }

    @Override
    public List<Post> getAll() {
        return new ArrayList<>(posts.values());
    }

    @Override
    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(posts.get(id));
    }
}