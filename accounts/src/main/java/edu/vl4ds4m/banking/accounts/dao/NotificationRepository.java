package edu.vl4ds4m.banking.accounts.dao;

import edu.vl4ds4m.banking.accounts.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query(value = """
            SELECT n.* FROM notification n
            ORDER BY n.instant ASC LIMIT ?1
            FOR UPDATE SKIP LOCKED""",
            nativeQuery = true) // TODO see Notification entity
    List<Notification> findTopKOrderByInstantAsc(int k);
}
