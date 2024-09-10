package oleg.sichev.theideafactory.repository;

import oleg.sichev.theideafactory.entity.TheIdeaFactoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheIdeaFactoryRepository extends JpaRepository<TheIdeaFactoryEntity, Long> {
    List<TheIdeaFactoryEntity> findByApproved(boolean approved);
    List<TheIdeaFactoryEntity> findByRejected(boolean rejected);
    List<TheIdeaFactoryEntity> findByIsDeletedFalse();

    List<TheIdeaFactoryEntity> findByIsDeletedTrue();

    List<TheIdeaFactoryEntity> findAll();

    @Query("SELECT e FROM TheIdeaFactoryEntity e LEFT JOIN FETCH e.comments")
    List<TheIdeaFactoryEntity> findAllEntriesWithComments();
}
