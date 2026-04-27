package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class UserRepository {

    private final Map<Long, User> users = new HashMap<>();

    public User createUser(User user) {
        long id = getNextId();

        user.setId(id);
        users.put(id, user);

        return user;
    }

    public User updateUser(User user) {
        return users.put(user.getId(), user);
    }

    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public void deleteUserById(Long userId) {
        users.remove(userId);
    }

    public Boolean isEmailExists(String email) {
        return users.values()
                .stream()
                .anyMatch(user -> email.equals(user.getEmail()));
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
