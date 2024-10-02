package oleg.sichev.theideafactory.repository;

import oleg.sichev.theideafactory.entity.Like;
import oleg.sichev.theideafactory.entity.TheIdeaFactoryEntity;
import oleg.sichev.theideafactory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserAndTheIdeaFactoryEntity(User user, TheIdeaFactoryEntity theIdeaFactoryEntity);

    List<Like> findAllByUser(User user);
}
