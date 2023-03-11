package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {
    private int userIdCount = 0;
    private final Map<Integer, User> userRepo = new HashMap<Integer, User>();

    public Map<Integer, User> getUserRepo() {
        return userRepo;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userRepo.values());
    }

    public User createUser(User user) {
        user.setId(++userIdCount);
        userRepo.put(userIdCount, user);
        return user;
    }

    public User getUserById(int userId) {
        return userRepo.get(userId);
    }

    public void deleteUser(int userId) {
        userRepo.remove(userId);
    }

    public User updateUser(User user, int userId) {
        User updatedUser = userRepo.get(userId);
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }
        userRepo.put(userId, updatedUser);
        return updatedUser;
    }
}
