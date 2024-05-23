package oleg.sichev.theideafactory.service;

import oleg.sichev.theideafactory.entity.User;
import oleg.sichev.theideafactory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

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
}
