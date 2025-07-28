package com.example.bug_tracker.controllers;

import com.example.bug_tracker.entities.Comment;
import com.example.bug_tracker.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/issues")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{bugId}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable String bugId,
                                              @RequestBody @Valid Comment comment) {
        return ResponseEntity.ok(commentService.addComment(bugId, comment));
    }

    @GetMapping("/{bugId}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable String bugId) {
        return ResponseEntity.ok(commentService.findCommentsByBugId(bugId));
    }
}
