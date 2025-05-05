package com.example.bug_tracker.controllers;

import com.example.bug_tracker.entities.Project;
import com.example.bug_tracker.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody @Valid Project project) {
        return ResponseEntity.ok(projectService.createProject(project));
    }

    @PutMapping("/{projectId}/users")
    public ResponseEntity<Project> assignUsersToProject(@PathVariable String projectId,
                                                        @RequestBody List<String> userIds) {
        return ResponseEntity.ok(projectService.assignUsersToProject(projectId, userIds));
    }

    @GetMapping
    public ResponseEntity<List<Project>> getProjectsForUser() {
        return ResponseEntity.ok(projectService.findProjectsForUser());
    }
}
