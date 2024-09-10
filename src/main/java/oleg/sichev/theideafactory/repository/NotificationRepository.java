package oleg.sichev.theideafactory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import oleg.sichev.theideafactory.entity.NotificationEntity;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByUserIdAndIsReadFalse(Long userId);

    // Новый метод для сортировки по времени создания
    List<NotificationEntity> findByUserIdAndIsReadFalseOrderByTimestampDesc(Long userId);

    // Метод для подсчета количества непрочитанных уведомлений
    int countByUserIdAndIsReadFalse(Long userId);
}