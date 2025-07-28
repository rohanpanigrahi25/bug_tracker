package com.example.bug_tracker.repositories;

import com.example.bug_tracker.entities.Milestone;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MilestoneRepository extends MongoRepository<Milestone, String> {
    List<Milestone> findByProjectId(String projectId);
}
