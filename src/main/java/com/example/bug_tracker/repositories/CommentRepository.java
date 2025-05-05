package com.example.bug_tracker.repositories;

import com.example.bug_tracker.entities.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByBugId(String bugId);
    List<Comment> findByBugIdOrderByTimestampDesc(String bugId);
}
