package com.example.carsellingshop.Services;

import com.example.carsellingshop.Model.User;
import com.example.carsellingshop.Repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public long registerUser(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty() ||
                user.getEmail() == null || user.getEmail().trim().isEmpty() ||
                user.getHashedPassword() == null || user.getHashedPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Username, email, and password are required");
        }
        String hashedPassword = BCrypt.hashpw(user.getHashedPassword(), BCrypt.gensalt());
        user.setHashedPassword(hashedPassword);
        return userRepository.insertUser(user);
    }

    public boolean authenticateUser(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return false;
        }
        User user = userRepository.getUserByUsername(username);
        return user != null && BCrypt.checkpw(password, user.getHashedPassword());
    }

    public User getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    public void updateUser(User user) {
        if (user.getId() <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        userRepository.updateUser(user);
    }

    public void deleteUser(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        userRepository.deleteUser(id);
    }
}