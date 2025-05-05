package com.example.bug_tracker.services;

import com.example.bug_tracker.config.CustomUserDetails;
import com.example.bug_tracker.entities.Bug;
import com.example.bug_tracker.entities.Comment;
import com.example.bug_tracker.entities.Project;
import com.example.bug_tracker.repositories.BugRepository;
import com.example.bug_tracker.repositories.CommentRepository;
import com.example.bug_tracker.repositories.ProjectRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BugRepository bugRepository;
    private final ProjectRepository projectRepository;
    private final NotificationService notificationService;

    @Transactional
    public Comment addComment(String bugId, @Valid Comment comment) {
        String userId = getCurrentUserId();
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new IllegalArgumentException("Bug not found"));
        validateProjectMembership(bug.getProjectId(), userId);
        comment.setBugId(bugId);
        comment.setUserId(userId);
        comment.setTimestamp(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);
        notificationService.createNotification(
                bug.getReporterId(),
                bugId,
                "New comment on bug: " + bug.getTitle()
        );
        return savedComment;
    }

    public List<Comment> findCommentsByBugId(String bugId) {
        String userId = getCurrentUserId();
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new IllegalArgumentException("Bug not found"));
        validateProjectMembership(bug.getProjectId(), userId);
        return commentRepository.findByBugIdOrderByTimestampDesc(bugId);
    }

    private void validateProjectMembership(String projectId, String userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        if (!project.getUserIds().contains(userId)) {
            throw new SecurityException("User is not a member of the project");
        }
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new SecurityException("User not authenticated");
        }
        return ((CustomUserDetails) authentication.getPrincipal()).getId();
    }
}
