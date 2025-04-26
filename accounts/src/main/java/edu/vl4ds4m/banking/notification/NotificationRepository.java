package edu.vl4ds4m.banking.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query(
        value = "SELECT n.* FROM " + Notification.TABLE_NAME + " n "
              + "ORDER BY n." + Notification.TIME + " ASC "
              + "LIMIT ?1 "
              + "FOR UPDATE SKIP LOCKED",
        nativeQuery = true)
    List<Notification> findNextK(int k);
}
