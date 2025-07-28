package com.example.bug_tracker.repositories;

import com.example.bug_tracker.entities.Label;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabelRepository extends MongoRepository<Label, String> {
    List<Label> findByBugId(String bugId);
    List<Label> findByName(String name);
}
