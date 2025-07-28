package com.example.bug_tracker.controllers;

import com.example.bug_tracker.entities.Milestone;
import com.example.bug_tracker.services.MilestoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/milestones")
@RequiredArgsConstructor
public class MilestoneController {

    private final MilestoneService milestoneService;

    @PostMapping
    public ResponseEntity<Milestone> createMilestone(@RequestBody @Valid Milestone milestone) {
        return ResponseEntity.ok(milestoneService.createMilestone(milestone));
    }

    //working only for admin users but intended to work for all the users who are in the project
    @GetMapping
    public ResponseEntity<List<Milestone>> getMilestones(@RequestParam String projectId) {
        return ResponseEntity.ok(milestoneService.findMilestonesByProjectId(projectId));
    }
}
