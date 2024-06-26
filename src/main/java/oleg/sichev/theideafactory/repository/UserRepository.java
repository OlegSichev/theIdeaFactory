package oleg.sichev.theideafactory.repository;

import oleg.sichev.theideafactory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsernameOrEmail(String username, String email);

    Optional<User> findByUsername(String username);
}
