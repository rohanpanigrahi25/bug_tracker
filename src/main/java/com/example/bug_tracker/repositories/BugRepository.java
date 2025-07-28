package com.example.bug_tracker.repositories;

import com.example.bug_tracker.entities.Bug;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BugRepository extends MongoRepository<Bug, String> {
    List<Bug> findByProjectId(String projectId);
    List<Bug> findByStatus(Bug.Status status);
    List<Bug> findByPriority(Bug.Priority priority);
    List<Bug> findByReporterId(String reporterId);
    List<Bug> findByAssigneeIdsContaining(String assigneeId);
    List<Bug> findByReporterIdOrAssigneeIdsContaining(String reporterId, String assigneeId);
    List<Bug> findByProjectIdAndStatus(String projectId, Bug.Status status);
    List<Bug> findByProjectIdAndPriority(String projectId, Bug.Priority priority);
    List<Bug> findByProjectIdAndStatusAndPriority(String projectId, Bug.Status status, Bug.Priority priority);
}

