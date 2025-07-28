package com.example.bug_tracker.controllers;

import com.example.bug_tracker.entities.Bug;
import com.example.bug_tracker.services.BugService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/issues")
@RequiredArgsConstructor
public class BugController {

    private final BugService bugService;

    @PostMapping
    public ResponseEntity<Bug> createBug(@RequestBody @Valid Bug bug) {
        return ResponseEntity.ok(bugService.createBug(bug));
    }

    @PutMapping("/{bugId}/assign")
    public ResponseEntity<Bug> assignBug(@PathVariable String bugId,
                                         @RequestBody List<String> assigneeIds) {
        return ResponseEntity.ok(bugService.assignBug(bugId, assigneeIds));
    }

    @PutMapping("/{bugId}/status")
    public ResponseEntity<Bug> updateStatus(@PathVariable String bugId,
                                            @RequestBody StatusRequest statusRequest) {
        return ResponseEntity.ok(bugService.updateStatus(bugId, statusRequest.getStatus()));
    }

    @GetMapping
    public ResponseEntity<List<Bug>> getBugs(@RequestParam String projectId,
                                             @RequestParam(required = false) Bug.Status status,
                                             @RequestParam(required = false) Bug.Priority priority) {
        return ResponseEntity.ok(bugService.findBugs(projectId, status, priority));
    }

    @GetMapping("/my-issues")
    public ResponseEntity<List<Bug>> getMyIssues() {
        return ResponseEntity.ok(bugService.findMyIssues());
    }
}

@Getter
@Setter
class StatusRequest {
    private Bug.Status status;
}
