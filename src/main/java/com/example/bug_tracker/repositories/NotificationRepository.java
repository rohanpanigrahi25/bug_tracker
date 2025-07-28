package com.example.bug_tracker.repositories;

import com.example.bug_tracker.entities.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByUserId(String userId);
    List<Notification> findByUserIdAndReadFalse(String userId);
    List<Notification> findByUserIdOrderByTimestampDesc(String userId);
}
