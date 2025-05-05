package com.example.bug_tracker.services;

import com.example.bug_tracker.config.CustomUserDetails;
import com.example.bug_tracker.entities.Milestone;
import com.example.bug_tracker.entities.Project;
import com.example.bug_tracker.repositories.MilestoneRepository;
import com.example.bug_tracker.repositories.ProjectRepository;
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
public class MilestoneService {
    private final MilestoneRepository milestoneRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Milestone createMilestone(@Valid Milestone milestone) {
        validateProjectExists(milestone.getProjectId());
        return milestoneRepository.save(milestone);
    }

    public List<Milestone> findMilestonesByProjectId(String projectId) {
        String userId = getCurrentUserId();
        validateProjectMembership(projectId, userId);
        return milestoneRepository.findByProjectId(projectId);
    }

    private void validateProjectExists(String projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Project not found");
        }
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
