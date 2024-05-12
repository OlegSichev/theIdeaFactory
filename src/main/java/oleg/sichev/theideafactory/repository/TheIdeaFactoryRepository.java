package oleg.sichev.theideafactory.repository;

import oleg.sichev.theideafactory.entity.TheIdeaFactoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheIdeaFactoryRepository extends JpaRepository<TheIdeaFactoryEntity, Long> {}