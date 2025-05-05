package com.example.bug_tracker.services;

import com.example.bug_tracker.config.CustomUserDetails;
import com.example.bug_tracker.entities.Project;
import com.example.bug_tracker.repositories.ProjectRepository;
import com.example.bug_tracker.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Project createProject(@Valid Project project) {
        Project savedProject = projectRepository.save(project);
        notifyProjectUsers(savedProject, "You have been assigned to project: " + project.getName());
        return savedProject;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Project assignUsersToProject(String projectId, List<String> userIds) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        userIds.forEach(userId -> {
            if (!userRepository.existsById(userId)) {
                throw new IllegalArgumentException("User not found: " + userId);
            }
        });
        project.getUserIds().addAll(userIds);
        Project updatedProject = projectRepository.save(project);
        notifyProjectUsers(updatedProject, "You have been assigned to project: " + project.getName());
        return updatedProject;
    }

    public List<Project> findProjectsForUser() {
        String userId = getCurrentUserId();
        return projectRepository.findByUserIdsContaining(userId);
    }

    private void notifyProjectUsers(Project project, String message) {
        project.getUserIds().forEach(userId ->
                notificationService.createNotification(userId, null, message));
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new SecurityException("User not authenticated");
        }
        return ((CustomUserDetails) authentication.getPrincipal()).getId();
    }
}
