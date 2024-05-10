package oleg.sichev.theideafactory.repository;

import oleg.sichev.theideafactory.entity.theIdeaFactoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface theIdeaFactoryRepository extends JpaRepository<theIdeaFactoryEntity, Long> {}