package com.example.bug_tracker.services;

import com.example.bug_tracker.config.CustomUserDetails;
import com.example.bug_tracker.entities.Bug;
import com.example.bug_tracker.entities.Project;
import com.example.bug_tracker.repositories.BugRepository;
import com.example.bug_tracker.repositories.ProjectRepository;
import com.example.bug_tracker.repositories.UserRepository;
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
public class BugService {
    private final BugRepository bugRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public Bug createBug(@Valid Bug bug) {
        String userId = getCurrentUserId();
        validateProjectMembership(bug.getProjectId(), userId);
        bug.setReporterId(userId);
        bug.setStatus(Bug.Status.OPEN);
        bug.setTimestamp(LocalDateTime.now());
        Bug savedBug = bugRepository.save(bug);
        notificationService.createNotification(
                userId,
                savedBug.getId(),
                "Bug reported: " + bug.getTitle()
        );
        return savedBug;
    }

    @Transactional
    public Bug assignBug(String bugId, List<String> assigneeIds) {
        String userId = getCurrentUserId();
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new IllegalArgumentException("Bug not found"));
        validateProjectMembership(bug.getProjectId(), userId);
        assigneeIds.forEach(assigneeId -> {
            if (!userRepository.existsById(assigneeId)) {
                throw new IllegalArgumentException("User not found: " + assigneeId);
            }
            validateProjectMembership(bug.getProjectId(), assigneeId);
        });
        bug.getAssigneeIds().addAll(assigneeIds);
        Bug updatedBug = bugRepository.save(bug);
        assigneeIds.forEach(assigneeId ->
                notificationService.createNotification(
                        assigneeId,
                        bugId,
                        "Assigned to bug: " + bug.getTitle()
                ));
        return updatedBug;
    }

    @Transactional
    public Bug updateStatus(String bugId, Bug.Status status) {
        String userId = getCurrentUserId();
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new IllegalArgumentException("Bug not found"));
        if (!bug.getReporterId().equals(userId) && !bug.getAssigneeIds().contains(userId)) {
            throw new SecurityException("Only reporter or assignees can update status");
        }
        bug.setStatus(status);
        Bug updatedBug = bugRepository.save(bug);
        notificationService.createNotification(
                bug.getReporterId(),
                bugId,
                "Bug status updated to " + status + ": " + bug.getTitle()
        );
        return updatedBug;
    }

    public List<Bug> findBugs(String projectId, Bug.Status status, Bug.Priority priority) {
        String userId = getCurrentUserId();
        validateProjectMembership(projectId, userId);
        if (status != null && priority != null) {
            return bugRepository.findByProjectIdAndStatusAndPriority(projectId, status, priority);
        } else if (status != null) {
            return bugRepository.findByProjectIdAndStatus(projectId, status);
        } else if (priority != null) {
            return bugRepository.findByProjectIdAndPriority(projectId, priority);
        }
        return bugRepository.findByProjectId(projectId);
    }

    public List<Bug> findMyIssues() {
        String userId = getCurrentUserId();
        return bugRepository.findByReporterIdOrAssigneeIdsContaining(userId, userId);
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
