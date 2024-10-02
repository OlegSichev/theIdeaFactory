package oleg.sichev.theideafactory.repository;

import oleg.sichev.theideafactory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsernameOrEmail(String username, String email); // Изменено на Optional

    Optional<User> findByUsername(String username);

    Optional<User> findById(Integer id); // Добавление метода findById
}
