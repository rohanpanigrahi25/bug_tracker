package com.example.bug_tracker.repositories;

import com.example.bug_tracker.entities.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {
    List<Project> findByUserIdsContaining(String userId);
}
