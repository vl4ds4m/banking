package org.vl4ds4m.banking.accounts.deprecation.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query(
        value = "SELECT n.* FROM " + Notification.TABLE_NAME + " n "
              + "ORDER BY n." + Notification.TIME + " ASC "
              + "LIMIT ?1 "
              + "FOR UPDATE SKIP LOCKED",
        nativeQuery = true)
    List<Notification> findNextK(int k);
}
