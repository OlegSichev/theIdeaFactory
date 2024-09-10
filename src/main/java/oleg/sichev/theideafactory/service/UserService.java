package oleg.sichev.theideafactory.service;

import oleg.sichev.theideafactory.entity.User;
import oleg.sichev.theideafactory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Добавляем dependency injection для PasswordEncoder

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Integer id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.orElse(null);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(Math.toIntExact(id));
    }

    public User findByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        return optionalUser.orElse(null);
    }

    public Integer getUserIdByUsername(String username) {
        User user = findByUsername(username);
        return user != null ? user.getId() : null; // Замените getId() на метод, который у вас есть в User
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return findByUsername(currentUsername);
    }

    // Метод для обновления пароля пользователя
    public void updatePassword(User user, String newPassword) {
        // Хешируем новый пароль
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);
        // Сохраняем изменения
        userRepository.save(user);
    }
}