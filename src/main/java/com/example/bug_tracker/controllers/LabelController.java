package com.example.bug_tracker.controllers;

import com.example.bug_tracker.entities.Label;
import com.example.bug_tracker.services.LabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/issues")
@RequiredArgsConstructor
public class LabelController {

    private final LabelService labelService;

    @PostMapping("/{bugId}/labels")
    public ResponseEntity<Label> addLabel(@PathVariable String bugId,
                                          @RequestBody @Valid Label label) {
        return ResponseEntity.ok(labelService.addLabel(bugId, label));
    }

    @GetMapping("/{bugId}/labels")
    public ResponseEntity<List<Label>> getLabels(@PathVariable String bugId) {
        return ResponseEntity.ok(labelService.findLabelsByBugId(bugId));
    }
}
