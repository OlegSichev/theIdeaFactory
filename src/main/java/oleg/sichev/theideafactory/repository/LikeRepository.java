package oleg.sichev.theideafactory.repository;

import oleg.sichev.theideafactory.entity.Like;
import oleg.sichev.theideafactory.entity.TheIdeaFactoryEntity;
import oleg.sichev.theideafactory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByEntryAndUser(TheIdeaFactoryEntity entry, User user);
}