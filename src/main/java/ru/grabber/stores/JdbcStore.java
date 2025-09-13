package ru.grabber.stores;

import ru.grabber.model.Post;
import ru.grabber.service.Store;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcStore implements Store {

    private final Connection connection;

    public JdbcStore(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO post(name, link, description, created) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, post.getName());
            ps.setString(2, post.getLink());
            ps.setString(3, post.getDescription());
            ps.setTimestamp(4, Timestamp.valueOf(
                    post.getCreated() != null ? post.getCreated() : LocalDateTime.now()
            ));

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error when saving post", e);
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT id, name, link, description, created FROM post");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                posts.add(new Post(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("link"),
                        rs.getString("description"),
                        rs.getTimestamp("created").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error when getAll", e);
        }
        return posts;
    }

    @Override
    public Optional<Post> findById(Long id) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT id, name, link, description, created FROM post WHERE id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Post(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("link"),
                            rs.getString("description"),
                            rs.getTimestamp("created").toLocalDateTime()
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error when findById", e);
        }
        return Optional.empty();
    }
}